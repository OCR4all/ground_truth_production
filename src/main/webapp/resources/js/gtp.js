$(document).ready(function() {
    // Set width of GT input field to the width of its mirror span element + a static buffer
    function adjustGTInputWidth(inputEl) {
        var width = $(inputEl).prev().width() + 40;
        if( width < 200 ) width = 400;
        $(inputEl).width(width);
    }

    // Load pages and update select
    function loadPages() {
        var curPageId = $('#pageId').val();
        $.get( "ajax/groundtruthdata/pages", { "pagesDir" : $("#dataDir").val() })
            .done(function( data ) {
                $('#pageId').find('option:not(:first)').remove();
                $.each(data, function(index, pageId) {
                    $('#pageId').append('<option value="' + pageId + '">' + pageId + '</option>');
                });

                if( curPageId == "null" ) {
                    // Select first page as it is loaded by default
                    $('#pageId option:eq(1)').prop('selected', true);
                }
                else {
                    $('#pageId option[value="' + curPageId + '"]').prop('selected', true);
                }
            })
            .fail(function( data ) {
                //TODO: Error handling
            });
    }

    // Handle checkbox to hide/display recognized text (gtText)
    $('#gtDisplay').on('change', function() {
        var gtElements = $('[data-content="gt"]');
        if( $(gtElements).first().is(":visible") ) {
            $(gtElements).hide();
        }
        else {
            $(gtElements).css("display", "block");
        }
    });

    // Adjust content of GT input mirror span and adjust the input width accordingly
    $('#lineList').on('keyup keypress blur change', 'input', function(event) {
        $(this).prev().text($(this).val());
        adjustGTInputWidth(this);
    });

    // Reload ground truth data if another page should be loaded
    $('#pageId').on('change', function() {
        var pageId = $(this).val();
        if( pageId == "null" )
            return;

        loadData();
    });
    $('#prevPage').on('click', function() {
        var prevEl = $('#pageId option:selected').prev('option');
        if (prevEl.length > 0 && prevEl.val() != "null")
            $(prevEl).prop('selected', true).change();
    });
    $('#nextPage').on('click', function() {
        var nextEl = $('#pageId option:selected').next('option');
        if (nextEl.length > 0)
            $(nextEl).prop('selected', true).change();
    });

    // Function to load and display the Ground Truth data (left side of the page)
    function loadData() {
        // Indicate change with clearing lines
        $('#lineList').empty();

        var dirType = $('#dirType').val();
        $.get( "ajax/groundtruthdata/load", { "dataDir" : $("#dataDir").val(), "dirType" : dirType, "pageId" : $('#pageId').val() } )
            .done(function( data ) {
                $.each(data, function(index, lineData) {
                    var ocr = lineData.ocr;
                    if( ocr === null ) {
                        ocr = (ocr === null) ? '' : ocr;
                    }

                    var gtText = lineData.gt;
                    var gtClass = "";
                    if( gtText === null ) {
                        gtText = ocr;
                    }
                    else {
                        gtClass = "has-gt";
                    }

                    var li = '<li id="' + lineData.id + '">';
                    li    += '<span class="lineId">' + lineData.id + '</span><br />';
                    li    += '<img src="data:image/jpeg;base64, ' + lineData.image + '" />';
                    li    += '<span class="asw-font" data-content="ocr"  style="display: none;">' + ocr + '</span><br />';
                    li    += '<span class="asw-font" data-content="gt" style="display: none;">' + gtText + '</span>';
                    li    += '<input type="text" data-id="' + lineData.id + '" class="asw-font ' + gtClass + '" value="' + gtText + '" />';
                    li    += '</li>';
                    $('#lineList').append(li);

                    // Adjust input width to width of its mirror span element
                    adjustGTInputWidth($('#lineList li').last().find('input').last());
                });

                // Directory type specific handling
                if( dirType == "pages" ) {
                    loadPages();
                    $('#pageSelection').css("display", "inline");
                }
                else {
                    $('#pageSelection').hide();
                }
            })
            .fail(function( data ) {
                //TODO: Error handling
            });
    }

    // Save Ground Truth data when input field changes
    $('#lineList').on('focusout', 'input', function(event) {
        var input = this;
        $.post("ajax/groundtruthdata/save", {
            "lineId" : $(input).attr('data-id'),
            "gtText"   : encodeURIComponent($(input).val())
        })
            .done(function( data ) {
                $(input).addClass("has-gt");
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
        if( $("#dataDir").val() === "" )
            return;

        loadData();
    });
    // Load project intially
    $("#loadProject").click();
});