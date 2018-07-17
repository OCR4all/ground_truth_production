<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<t:html>
    <t:head>
        <title>Ground Truth Production</title>
    </t:head>

    <t:body>
        <div id="setup">
            <div id="pathSetting">
                Directory Path: <input type="text" id="dataDir" name="dataDir" value="${dataDir}"/>

                <c:choose>
                    <c:when test='${dirType == "pages"}'><c:set value='selected="selected"'
                                                                var="pagesSel"></c:set></c:when>
                    <c:otherwise><c:set value='selected="selected"' var="flatSel"></c:set></c:otherwise>
                </c:choose>
                <select id="dirType">
                    <option value="flat" ${flatSel}>Flat directory</option>
                    <option value="pages" ${pagesSel}>Pages directory</option>
                </select>

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
                    <input id="configFile" name="configFile" type="file" style="display: none;"/>
                    <button id="importConfig" type="submit">Import</button>
                    <button id="exportConfig" type="submit">Export</button>
                </div>
            </div>
        </div>

        <div id="wrapper">
            <div id="content">
                <div id="viewSetting">
                    <div id="gtDisplaySelection">
                        Show recognized text:
                        <input id="gtDisplay" type="checkbox"/>
                    </div>
                    <div id="pageSelection">
                        Select page:
                        <button id="prevPage">Prev</button>
                        <select id="pageId">
                            <option value="null">-- select --</option>
                        </select>
                        <button id="nextPage">Next</button>
                    </div>
                </div>
                <ul id="lineList"></ul>
            </div>
            <div id="settings">
                <div id="addWidget">
                    <a href="#addWidgetModal" rel="modal:open"></a>
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

        <div id="addWidgetModal" class="modal">
            <h2>Adding new button</h2>
            <span>Enter character:</span><br/>
            <input type="text" id="newWidgetChar" name="newWidgetChar" value="" maxlength="1" size="1"
                   class="asw-font"/>
            <p class="error-text">Please enter a character to continue!</p>
            <br/><br/>
            <button id="addWidgetToGrid">Add to grid</button>
        </div>
    </t:body>
</t:html>
