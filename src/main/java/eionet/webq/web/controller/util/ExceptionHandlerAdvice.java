package eionet.webq.web.controller.util;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger LOGGER = Logger.getLogger("webq2");
    private static final String EXCEPTION_STATUS = "status";
    private static final String EXCEPTION_MESSAGE = "errorMessage";
    public static final String ERROR_VIEW = "/error";

    @ExceptionHandler(Exception.class)
    public ModelAndView genericExceptionHandler(Exception exception) {
        LOGGER.error("An error has occurred", exception);
        ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView maxUploadSizeExceptionHandler(MaxUploadSizeExceededException exception) {
        LOGGER.warn(exception);
        ModelAndView modelAndView = new ModelAndView(ERROR_VIEW);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.BAD_REQUEST.value());
        String exceptionMessage = String.format("Maximum size of file exceeded. Allowed size is %d megabytes", (exception.getMaxUploadSize() / 1024 / 1024));
        modelAndView.addObject(EXCEPTION_MESSAGE, exceptionMessage);
        return modelAndView;
    }
}
