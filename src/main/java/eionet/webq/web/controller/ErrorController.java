package eionet.webq.web.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/general")
public class ErrorController {

    private static final Logger LOGGER = Logger.getLogger("webq2");
    private static final String EXCEPTION_STATUS = "status";
    private static final String EXCEPTION_MESSAGE = "errorMessage";
    private static final String ERROR_VIEW = "/error";

    @GetMapping("/errors")
    public ModelAndView renderDefaultErrorPage(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        System.out.println(session);
        int errorStatus = getErrorCode(httpRequest);
        String errorMsg = getErrorMessage(errorStatus);

        LOGGER.error("An error has occurred: " + errorStatus + " " + errorMsg);
        ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);
        modelAndView.setStatus(HttpStatus.valueOf(errorStatus));
        modelAndView.addObject(EXCEPTION_STATUS, errorStatus);
        modelAndView.addObject(EXCEPTION_MESSAGE, errorMsg);
        return modelAndView;
    }

    int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    String getErrorMessage(int errorStatus) {
        String errorMsg = "";
        switch (errorStatus) {
            case 400: {
                errorMsg = "Bad Request";
                break;
            }
            case 403: {
                errorMsg = "Forbidden";
                break;
            }
            case 404: {
                errorMsg = "Not Found";
                break;
            }
            case 500: {
                errorMsg = "Internal Server Error";
                break;
            }
        }
        return errorMsg;
    }
}
