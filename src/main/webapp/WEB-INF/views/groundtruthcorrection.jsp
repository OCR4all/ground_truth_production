<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <script type="text/javascript">
            $(document).ready(function() {
                // Function to load and display the Ground Truth data (left side of the page)
                function loadGroundTruthData() {
                    $.get( "ajax/groundtruthdata", { "gtcDir" : $("#gtcDir").val() } )
                    .done(function( data ) {
                        var maxWidth = 0;
                        $.each(data, function(index, lineData) {
                            var gtcText = lineData.groundTruthCorrection;
                            if( gtcText === null )
                                gtcText = lineData.groundTruth;

                            var li = '<li id="' + lineData.id + '">';
                            li    += '<span class="lineId">' + lineData.id + '</span><br />';
                            li    += '<img src="data:image/jpeg;base64, ' + lineData.image + '" /><br />';
                            li    += '<span class="asw-font">' + lineData.groundTruth + '</span><br />';
                            li    += '<input type="text" class="asw-font" value="' + gtcText + '" />';
                            li    += '</li>';
                            $('#lineList').append(li);

                            // Update max text width
                            var textWidth = $('#lineList li').last().find('span').last().width();
                            if( textWidth > maxWidth)
                                maxWidth = textWidth;
                        });

                        // Set width of all input fields to widest span + static buffer
                        $('#lineList input').width(maxWidth + 40);
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                }

                function updateGrid(grid, itemData) {
                    // Clear grid before loading all items
                    grid.removeAll();
                    $.each(itemData, function(index, item) {
                        grid.addWidget(
                            $('<div><div class="grid-stack-item-content"><button class="asw-font">'  + item.content + '</button></div><div/>'),
                            item.x, item.y, item.width, item.height
                        );
                    });
                }
                // Function to load and display the virtual keyboard (right side of the page)
                function loadVirtualKeyboard() {
                    $.get( "ajax/virtualkeyboard/load", { "keyType" : "complete" } )
                    .done(function( data ) {
                        if( data === null) {
                            //TODO: Error handling
                            return;
                        }

                        var grid = $('.grid-stack').data('gridstack');
                        updateGrid(grid, data);
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                }

                // Split page in two parts
                var split = Split(['#content', '#settings'], {
                    sizes: [ 60, 40 ],
                    minSize: [ 200, 470 ],
                });
                // Collapse/Open virtual keyboard on gutter double click
                $('.gutter').dblclick(function() {
                   if( $('#settings').is(':visible') ) {
                       $('#settings').hide();
                       split.collapse(1);
                   }
                   else {
                       $('#settings').show();
                       split.setSizes( [60, 40] );
                   }
                });

                // Import/Export virtual keyboard configuration
                $('#configFile').on('change', function() {
                    var fileList = this.files;
                    if( fileList === undefined || fileList.length !== 1 ) {
                        //TODO: Error handling
                        return;
                    }

                    var configFile = fileList[0];
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        if( e.target === undefined || e.target.result === "" ) {
                            //TODO: Error handling
                            return;
                        }

                        // Read JSON file 
                        jQuery.when(jQuery.getJSON(e.target.result))
                        .done(function(json) {
                            if( json === undefined ) {
                                //TODO: Error handling
                                return;
                            }

                            // Update virtual keyboard
                            var grid = $('.grid-stack').data('gridstack');
                            updateGrid(grid, json);
                        });
                    };
                    // Load the file
                    reader.readAsDataURL(configFile);
                });
                $('#importConfig').click(function() {
                    $('#configFile').click();
                });
                $('#exportConfig').click(function() {
                    // Fetch data of all keys
                    var keys = [];
                    $.each($('.grid-stack-item'), function(index, el) {
                        keys.push({
                            'x':       $(this).attr('data-gs-x'),
                            'y':       $(this).attr('data-gs-y'),
                            'width':   $(this).attr('data-gs-width'),
                            'height':  $(this).attr('data-gs-height'),
                            'content': $(this).text(),
                        });
                    });

                    // Create temporary download element (needs to be <a>)
                    var downloadEl   = document.createElement("a");
                    downloadEl.href = 'data:application/json;charset=utf-8,' + JSON.stringify(keys);
                    downloadEl.download = 'virtualKeyboardConfig.json';
                    document.body.appendChild(downloadEl);
                    // Start download and remove temporary element
                    downloadEl.click();
                    document.body.removeChild(downloadEl);
                });

                // Click behavior for virtual keyboard buttons
                // If the user has a focused input field allow key usage
                var lastInput = null;
                var lastPosition = 0;
                $('#lineList').on('click', 'input', function() {
                    lastInput = this;
                    lastPosition = $(this).prop('selectionStart');
                });
                $('#lineList').on('focusout', 'input', function(event) {
                    var newFocus = event.relatedTarget;
                    // Virtual keyboard button is pressed
                    if( newFocus != null && $(newFocus).parents('.grid-stack').length === 1 )
                        return;

                    // Any other element was clicked/selected, so reset last input
                    lastInput = null;
                    lastPosition = 0;
                });
                $('.grid-stack').on('click', 'button', function() {
                    if( lastInput == null )
                        return;

                    // Insert clicked character to last input field at last position
                    var oldText = $(lastInput).val();
                    var newText = oldText.substr(0, lastPosition) + $(this).text() + oldText.substr(lastPosition);
                    $(lastInput).val(newText);
                    // Focus input and set cursor position after new character
                    $(lastInput).focus();
                    lastPosition++;
                    $(lastInput).prop('selectionStart', lastPosition);
                    $(lastInput).prop('selectionEnd', lastPosition);
                });

                // Lock/Unlock Grid
                $('#lockGrid').click(function() {
                    $.each($('.grid-stack span'), function(index, el) {
                        var content = $(el).html();
                        $(el).replaceWith('<button class="asw-font">' + content + '</button>');
                    });
                    var grid = $('.grid-stack').data('gridstack');
                    grid.enableMove(false);
                });
                $('#unlockGrid').click(function() {
                    $.each($('.grid-stack button'), function(index, el) {
                        var content = $(el).html();
                        $(el).replaceWith('<span class="asw-font">' + content + '</span>');
                    });
                    var grid = $('.grid-stack').data('gridstack');
                    grid.enableMove(true);
                });

                // Cutsomizeable grid for virtual keyboard
                var options = {
                    float: true,
                    disableResize: true,
                    cellHeight: 40,
                    cellWidth: 40,
                    verticalMargin: 5,
                    disableDrag: true,
                };
                $('.grid-stack').gridstack(options);
                // Load keyboard
                loadVirtualKeyboard();

                // Fetch and display page contents via AJAX
                $("#loadProject").click(function() {
                    if( $("#gtcDir").val() === "" )
                        return;

                    loadGroundTruthData();
                });
                // Load project intially
                $("#loadProject").click();
            });
        </script>
    </t:head>

    <t:body>
        <div id="setup">
            <div id="pathSetting">
                Ground Truth directory path: <input type="text" id="gtcDir" name="gtcDir" value="${gtcDir}" />
                <button id="loadProject" type="submit">Load</button>
            </div>
            <div id="keyboardSetting">
                <div id="gridSetting">Grid:
                    <button id="lockGrid" type="submit">Lock</button>
                    <button id="unlockGrid" type="submit">Unlock</button>
                </div>
                <div id="configSetting">Config:
                    <input id="configFile" name="configFile" type="file" style="display: none;" />
                    <button id="importConfig" type="submit">Import</button>
                    <button id="exportConfig" type="submit">Export</button>
                </div>
            </div>
        </div>

        <div id="wrapper">
            <div id="content"><ul id="lineList"></ul></div>
            <div id="settings"><div class="grid-stack"></div></div>
        </div>
    </t:body>
</t:html>
