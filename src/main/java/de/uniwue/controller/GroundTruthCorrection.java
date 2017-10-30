package de.uniwue.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uniwue.helper.ContentLoader;
import de.uniwue.model.LineData;

/**
 * Controller class for the Ground Truth page
 * Use response.setStatus to trigger AJAX fail (and therefore show errors)
 */
@Controller
public class GroundTruthCorrection {
    /**
     * Response to the request to send the content of the project root
     *
     * @param gtcDir Directory to the necessary ground truth files
     * @return Returns the content of the /groundtruthcorrection page
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showMessage(
                @RequestParam(value = "gtcDir", required = false) String gtcDir,
                HttpSession session
            ) {
        // If a gtcDir parameter is passed in URL add it to session
        if (gtcDir != null && !gtcDir.isEmpty())
            session.setAttribute("gtcDir", gtcDir);

        ModelAndView mv = new ModelAndView("groundtruthcorrection");
        return mv;
    }

    /**
     * Response to the request to send the contents of the given ground truth folder
     * This folder should contain lines that are represented as image and text
     *
     * @param gtcDir Directory to the necessary ground truth files
     * @return tbd
     */
    @RequestMapping(value = "/ajax/content" , method = RequestMethod.GET)
    public @ResponseBody ArrayList<LineData> jsonOverview(
                @RequestParam("gtcDir") String gtcDir,
                HttpSession session, HttpServletResponse response
            ) {
        // Store gtc directory in session (serves as entry point)
        session.setAttribute("gtcDir", gtcDir);

        ContentLoader cl = new ContentLoader(gtcDir);
        try {
            return cl.getContent();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
