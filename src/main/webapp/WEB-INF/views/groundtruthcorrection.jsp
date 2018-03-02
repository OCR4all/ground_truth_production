<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <script type="text/javascript">
            $(document).ready(function() {
                // Set width of GTC input field to the width of its mirror span element + a static buffer
                function adjustGTCInputWidth(inputEl) {
                    $(inputEl).width($(inputEl).prev().width() + 40);
                }

                // Adjust content of GTC input mirror span and adjust the input width accordingly 
                $('#lineList').on('keyup keypress blur change', 'input', function(event) {
                    $(this).prev().text($(this).val());
                    adjustGTCInputWidth(this);
                });

                // Function to load and display the Ground Truth data (left side of the page)
                function loadGroundTruthData() {
                    $.get( "ajax/groundtruthdata/load", { "gtcDir" : $("#gtcDir").val() } )
                    .done(function( data ) {
                        $('#lineList').empty();
                        $.each(data, function(index, lineData) {
                            var gtcText = lineData.groundTruthCorrection;
                            var gtcClass = "";
                            if( gtcText === null ) {
                                gtcText = lineData.groundTruth;
                            }
                            else {
                                gtcClass = "has-gtc-text";
                            }

                            var li = '<li id="' + lineData.id + '">';
                            li    += '<span class="lineId">' + lineData.id + '</span><br />';
                            li    += '<img src="data:image/jpeg;base64, ' + lineData.image + '" />';
                            li    += '<span class="asw-font" data-content="gt"  style="display: none;">' + lineData.groundTruth + '</span><br />';
                            li    += '<span class="asw-font" data-content="gtc" style="display: none;">' + gtcText + '</span>'
                            li    += '<input type="text" data-id="' + lineData.id + '" class="asw-font ' + gtcClass + '" value="' + gtcText + '" />';
                            li    += '</li>';
                            $('#lineList').append(li);

                            // Adjust input width to width of its mirror span element
                            adjustGTCInputWidth($('#lineList li').last().find('input').last());
                        });
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                }

                // Save Ground Truth data when input field changes
                $('#lineList').on('focusout', 'input', function(event) {
                    var input = this;
                    $.post("ajax/groundtruthdata/save", {
                        "gtcLineId" : $(input).attr('data-id'),
                        "gtcText"   : encodeURIComponent($(input).val()) 
                    })
                    .done(function( data ) {
                        $(input).addClass("has-gtc-text");
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                });

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

                // Load virtual keyboard
                initializeVirtualKeyboard($('.grid-stack'), 'complete');

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
                Directory Path: <input type="text" id="gtcDir" name="gtcDir" value="${gtcDir}" />
                <button id="loadProject" type="submit">Load</button>
            </div>
            <div id="keyboardSetting">
                <div id="gridSetting">Grid:
                    <button id="lockGrid" type="submit">Lock</button>
                    <button id="unlockGrid" type="submit">Edit</button>
                    <button id="saveGrid" type="submit">Save</button>
                    <button id="resetGrid" type="submit">Reset</button>
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
            <div id="settings">
                <div id="addWidget">
                    <span class="left">Click to add new button</span>
                    <span class="right">Click to add new button</span>
                </div>
                <div id="removeWidget" class="trash ui-droppable">
                    <span class="left">Drag button to delete</span>
                    <span class="right">Drag button to delete</span>
                </div>
                <div class="grid-stack"></div>
            </div>
        </div>
    </t:body>
</t:html>
