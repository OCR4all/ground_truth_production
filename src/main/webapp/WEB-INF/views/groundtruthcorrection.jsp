<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <script type="text/javascript">
            $(document).ready(function() {
                $("#loadProject").click(function() {
                    $.get( "ajax/content", { "gtcDir" : $("#gtcDir").val() } )
                    .done(function( data ) {
                        //TODO: Display project content on page
                    })
                    .fail(function( data ) {
                        //TODO: Error handling
                    });
                });
            });
        </script>
    </t:head>

    <t:body>
        <input type="text" id="gtcDir" name="gtcDir" value="${gtcDir}" />
        <button id="loadProject" type="submit">Load project</button>
    </t:body>
</t:html>
