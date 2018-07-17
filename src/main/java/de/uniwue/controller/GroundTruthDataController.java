package de.uniwue.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.uniwue.helper.DataHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uniwue.model.LineData;

/**
 * Controller class for the Ground Truth page
 * Use response.setStatus to trigger AJAX fail (and therefore show errors)
 */
@Controller
public class GroundTruthDataController {
    /**
     * Response to the request to send the content of the project root
     *
     * @param dataDir Directory to the necessary files
     * @param dirType Type of the data directory (flat | pages)
     * @param pageId Id of the current page if directory type = "pages"
     * @param session Session of the user
     * @return Returns the content of the /groundtruthcorrection page
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView displayGroundTruthCorrectionPage(
                @RequestParam(value = "dataDir", required = false) String dataDir,
                @RequestParam(value = "dirType", required = false) String dirType,
                @RequestParam(value = "pageId", required = false) String pageId,
                HttpSession session
            ) {
        // Store passed parameters in URL and add them to session
        if (dataDir != null && !dataDir.isEmpty())
            session.setAttribute("dataDir", dataDir);
        if (dirType != null && !dirType.isEmpty())
            session.setAttribute("dirType", dirType);

        ModelAndView mv = new ModelAndView("gtProduction");
        return mv;
    }

    /**
     * Response to the request to send the contents of the given ground truth folder
     * This folder should contain lines that are represented as image and text
     *
     * @param dataDir Directory to the necessary files
     * @param dirType Type of the data directory (flat | pages)
     * @param pageId Id of the current page if directory type = "pages" 
     * @param session Session of the user
     * @param response Response to the request
     * @return Ground Truth data (image, Ground Truth, Ground Truth correction)
     */
    @RequestMapping(value = "/ajax/groundtruthdata/load" , method = RequestMethod.GET)
    public @ResponseBody ArrayList<LineData> jsonLoadGroundTruthData(
                @RequestParam("dataDir") String dataDir,
                @RequestParam("dirType") String dirType,
                @RequestParam(value = "pageId", required = false) String pageId,
                HttpSession session, HttpServletResponse response
            ) {
        // Store parameters in session (serve as entry point)
        session.setAttribute("dataDir", dataDir);
        session.setAttribute("dirType", dirType);

        try {
            // Store DataHelper in session after loading
            DataHelper dataHelper = new DataHelper(dataDir, dirType, pageId);
            session.setAttribute("dataHelper", dataHelper);

            return dataHelper.getData();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    /**
     * Saves the Ground Truth data
     *
     * @param lineId line Id
     * @param gtText Corrected Ground Truth text
     * @param session Session of the user
     * @param response Response to the request
     */
    @RequestMapping(value = "/ajax/groundtruthdata/save" , method = RequestMethod.POST)
    public @ResponseBody void jsonSaveGroundTruthData(
                @RequestParam("lineId") String lineId,
                @RequestParam("gtText") String gtText,
                HttpSession session, HttpServletResponse response
            ) {
        try {
            DataHelper dataHelper = (DataHelper) session.getAttribute("dataHelper");
            dataHelper.saveGroundTruthData(lineId, URLDecoder.decode(gtText, StandardCharsets.UTF_8.toString()));
        } catch (IOException f) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Response to the request to send the pages of a the ground truth directory
     * Will be used if directory type = "pages" only
     *
     * @param pagesDir Pages directory
     * @param session Session of the user
     * @param response Response to the request
     * @return String array of pages
     */
    @RequestMapping(value = "/ajax/groundtruthdata/pages" , method = RequestMethod.GET)
    public @ResponseBody String[] jsonGetPages(
                @RequestParam("pagesDir") String pagesDir,
                HttpSession session,
                HttpServletResponse response
            ) {
        try {
            DataHelper dataHelper = (DataHelper) session.getAttribute("dataHelper");
            return dataHelper.getPages(pagesDir);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
