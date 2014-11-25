package eionet.webq.web.controller.util;

import eionet.webq.dao.orm.UserFile;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Helps to get user file from database based on request parameters.
 */
@Component
public class UserFileHelper {
    /**
     * Logger for this class.
     */
    public static final Logger LOGGER = Logger.getLogger(UserFileHelper.class);
    /**
     * Service for user uploaded files.
     */
    @Autowired
    private UserFileService userFileService;

    /**
     * Get user file from database, using HTTP session jsessionid attribute or sessionid request paramater.
     *
     * @param fileId  file id to fetch
     * @param request current request
     * @return user file object
     */
    public UserFile getUserFile(int fileId, HttpServletRequest request) {
        UserFile file = getOrDownloadUserFile(fileId, request, false);

        return file;
    }

    /**
     * Get user file from database, using HTTP session jsessionid attribute or sessionid request paramater.
     * Updates download time in database.
     *
     * @param fileId  file id to fetch
     * @param request current request
     * @return user file object
     */
    public UserFile downloadUserFile(int fileId, HttpServletRequest request) throws FileNotAvailableException {
        UserFile file = getOrDownloadUserFile(fileId, request, true);

        if (file == null) {
            throw new FileNotAvailableException("The requested user file is not available with fileId: " + fileId);
        }
        return file;
    }

    /**
     * Download UserFile object by id or by session ID. Updates download time in database.
     *
     * @param fileId   file id to fetch
     * @param request  current request
     * @param download updated download time in database or not
     * @return user file object
     */
    private UserFile getOrDownloadUserFile(int fileId, HttpServletRequest request, boolean download) {

        UserFile file = null;
        if (download) {
            file = userFileService.download(fileId);
        } else {
            file = userFileService.getById(fileId);
        }

        if (file == null && request.getParameter("sessionid") != null) {
            LOGGER.info("Could not find file by HTTP session ID. Let's try by sessionid parameter.");
            file = userFileService.getByIdAndUser(fileId, request.getParameter("sessionid"));
        }
        return file;
    }
}
