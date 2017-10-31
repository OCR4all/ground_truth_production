package de.uniwue.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.uniwue.helper.VirtualKeyboardHelper;

/**
 * Controller class to manage the virtual keyboard
 * Use response.setStatus to trigger AJAX fail (and therefore show errors)
 */
@Controller
public class VirtualKeyboardController {
    /**
     * Response to the request to send the layout and contents of the virtual keyboard
     *
     * @param keyType Determines which keys to get ("complete" or "frequent" ones)
     * @param session Session of the user
     * @param response Response to the request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/ajax/virtualkeyboard/load" , method = RequestMethod.GET)
    public @ResponseBody ArrayList<HashMap<String, Object>> jsonLoadVirtualKeyboard(
                @RequestParam("keyType") String keyType,
                HttpSession session, HttpServletResponse response
            ) {
        // Try to load virtual keyboard configuration from session first 
        HashMap<String, ArrayList<HashMap<String, Object>>> vkConf =
                (HashMap<String, ArrayList<HashMap<String, Object>>>) session.getAttribute("vkConf");
        if (vkConf != null && !vkConf.isEmpty()) {
            ArrayList<HashMap<String, Object>> keyTypeConf =
                    (ArrayList<HashMap<String, Object>>) vkConf.get(keyType);
            if (keyTypeConf != null && !keyTypeConf.isEmpty()) {
                return keyTypeConf;
            }
        }
        else {
            // Initialize vkConf session variable if it does not exist
            session.setAttribute("vkConf", new HashMap<String, ArrayList<HashMap<String, Object>>>());
            vkConf = (HashMap<String, ArrayList<HashMap<String, Object>>>) session.getAttribute("vkConf");
        }

        VirtualKeyboardHelper vk = new VirtualKeyboardHelper();
        List<List<String>> keys = vk.getKeys(keyType);
        if (keys == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }

        // Adjust key data structure to serialization requirements of gridstack.js
        ArrayList<HashMap<String, Object>> gridstack = new ArrayList<HashMap<String, Object>>();
        for(int y = 0; y < keys.size(); y++) {
            List<String> line = keys.get(y);
            for(int x = 0; x < line.size(); x++) {
                HashMap<String, Object> element = new HashMap<String, Object>();
                element.put("x", x);
                element.put("y", y);
                element.put("width", 1);
                element.put("height", 1);
                element.put("content", line.get(x));
                gridstack.add(element);
            }
        }

        // Update session with loaded configuration
        session.setAttribute("vkConf", vkConf.put(keyType, gridstack));

        return gridstack;
    }
}
