<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <script type="text/javascript">
            $(document).ready(function() {
                // Split page in two parts
                Split(['#content', '#settings'], {sizes: [60, 40] });

                $("#loadProject").click(function() {
                    if( $("#gtcDir").val() === "" )
                        return;

                    $.get( "ajax/content", { "gtcDir" : $("#gtcDir").val() } )
                    .done(function( data ) {
                        var maxWidth = 0;
                        $.each(data, function(index, lineData) {
                            var gtcText = lineData.groundTruthCorrection;
                            if( gtcText === null )
                                gtcText = lineData.groundTruth;

                            var li = '<li id="' + lineData.id + '">';
                            li    += '<img src="data:image/jpeg;base64, ' + lineData.image + '" /><br />';
                            li    += '<span class="asw-font">' + lineData.groundTruth + '</span><br />';
                            li    += '<input type="text" class="asw-font" value="' + gtcText + '" />';
                            li    += '</li>';
                            $('#lineList').append(li);

                            var textWidth = $('#lineList li').last().find('span').width();
                            if( textWidth > maxWidth)
                                maxWidth = textWidth;
                        });

                        // Set width of all input fields to widest span + static buffer
                        $('#lineList input').width(maxWidth + 40);
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                });

                // Load project intially
                $("#loadProject").click();

                $(function () {
                    var options = {
                        float: true,
                        disableResize: true,
                        cellHeight: 40,
                        cellWidth: 40,
                        verticalMargin: 5,
                    };
                    $('.grid-stack').gridstack(options);
                });
            });
        </script>
    </t:head>

    <t:body>
        <div id="setup">
            Ground Truth directory path: <input type="text" id="gtcDir" name="gtcDir" value="${gtcDir}" />
            <button id="loadProject" type="submit">Load</button>
        </div>

        <div id="wrapper">
            <div id="content"><ul id="lineList"></ul></div>
            <div id="settings">
                <div class="grid-stack">
                    <div class="grid-stack-item" data-gs-x="0" data-gs-y="0" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                    <div class="grid-stack-item" data-gs-x="1" data-gs-y="0" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                    <div class="grid-stack-item" data-gs-x="2" data-gs-y="0" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                    <div class="grid-stack-item" data-gs-x="1" data-gs-y="1" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                    <div class="grid-stack-item" data-gs-x="3" data-gs-y="1" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                    <div class="grid-stack-item" data-gs-x="2" data-gs-y="3" data-gs-width="1" data-gs-height="1">
                        <div class="grid-stack-item-content"></div>
                    </div>
                </div>
            </div>
        </div>
    </t:body>
</t:html>
