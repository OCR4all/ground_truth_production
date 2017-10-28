<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:html>
    <t:head>
        <title>Ground Truth Correction</title>

        <t:body>
            <form action="Edit" method="post">
                <input type="text" id="gtcDir" name="gtcDir" />
                <button type="submit">Go</button>
            </form>
        </t:body>
    </t:head>
</t:html>
