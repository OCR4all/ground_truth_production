/**
 * Includes jQuery functionality for project specific virtual keyboard
 *
 * Things that need to be done to use this:
 * 1. Include this file in the head of the target page (see head.tag)
 * 2. Add "initializeVirtualKeyboard(gridContainer, keyType)" function call to the target page (when document ready)
 * 3. For grid lock/unlock functionality add buttons with  id="lockGrid" and id="unlockGrid" to the page
 * 4. For configuration handling add buttons with id="importConfig" and id="exportConfig",
 *    as well as a hidden file input with id="configFile" to the page
 */

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

// Function to load and display the virtual keyboard (into grid-stack container)
function loadVirtualKeyboard(grid, keyType) {
    $.get( "ajax/virtualkeyboard/load", { "keyType" : keyType } )
    .done(function( data ) {
        if( data === null) {
            //TODO: Error handling
            return;
        }

        updateGrid(grid, data);
    })
    .fail(function( data ) {
        //TODO: Error handling
    });
}

function initializeVirtualKeyboard(gridContainer, keyType) {
    // Customizeable grid for virtual keyboard
    var options = {
        float: true,
        disableResize: true,
        cellHeight: 40,
        cellWidth: 40,
        verticalMargin: 5,
        disableDrag: true,
    };
    $(gridContainer).gridstack(options);

    var grid = $(gridContainer).data('gridstack');
    // Load keyboard
    loadVirtualKeyboard(grid, keyType);
}


$(document).ready(function() {
    // Get virtual keyboard configuration
    function virtualKeyboardKeys() {
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
        return keys;
    }

    // Save virtual keyboard to session
    function saveVirtualKeyboard(grid, jsonConf) {
        // Save current settings to session (to keep them on page reload)
        $.ajax({
            url: 'ajax/virtualkeyboard/savecomplete',
            type: 'post',
            data: JSON.stringify(jsonConf),
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            dataType: 'json',
        });
    }
    // Removes virtual keyboard from session and reload it
    function resetVirtualKeyboard(grid) {
        $.post('ajax/virtualkeyboard/reset')
        .done(function() {
            loadVirtualKeyboard(grid, "complete");
        })
        .fail(function() {
            //TODO: Error handling
        });
    }

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

                // Update and save virtual keyboard
                var grid = $('.grid-stack').data('gridstack');
                updateGrid(grid, jsonConf);
                saveVirtualKeyboard(grid, json);
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
        var keys = virtualKeyboardKeys();

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
    $('#lineList').on('focusout', 'input', function(event) {
        var newFocus = event.relatedTarget;
        // Virtual keyboard button is pressed
        if( newFocus != null && $(newFocus).parents('.grid-stack').length === 1 ) {
            lastInput = this;
            lastPosition = $(this).prop('selectionStart');
            return;
        }

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
        lastPosition = lastPosition + $(this).text().length;
        $(lastInput).prop('selectionStart', lastPosition);
        $(lastInput).prop('selectionEnd', lastPosition);
        $(lastInput).focus();
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

    // Save/Reset Grid
    $('#saveGrid').click(function() {
        // Save virtual keyboard to session
        var grid = $('.grid-stack').data('gridstack');
        saveVirtualKeyboard(grid, virtualKeyboardKeys());
    });
    $('#resetGrid').click(function() {
        // Reset virtual keyboard
        var grid = $('.grid-stack').data('gridstack');
        resetVirtualKeyboard(grid);
    });
});
