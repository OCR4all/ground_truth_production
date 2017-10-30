<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <script type="text/javascript">
            $(document).ready(function() {
                $("#loadProject").click(function() {
                    if( $("#gtcDir").val() === "" )
                        return;

                    $.get( "ajax/content", { "gtcDir" : $("#gtcDir").val() } )
                    .done(function( data ) {
                        $.each(data, function(index, lineData) {
                            var li = '<li id="' + lineData.id + '">';
                            li    += '<img src="data:image/jpeg;base64, ' + lineData.image + '" />';
                            li    += '<input type="text" class="asw-font" value="' + lineData.groundTruth + '" />';
                            li    += '<input type="text" class="asw-font" value="' + lineData.groundTruthCorrection + '" />';
                            li    += '</li>';
                            $('#lineList').append(li);
                        });
                        console.log(data);
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                        console.log(data);
                    });
                });

                // Load project intially
                $("#loadProject").click();
            });
        </script>
    </t:head>

    <t:body>
        <input type="text" id="gtcDir" name="gtcDir" value="${gtcDir}" />
        <button id="loadProject" type="submit">Load project</button>

        <div id="content"><ul id="lineList"></ul></div>
        <div id="settings"></div>
    </t:body>
</t:html>
