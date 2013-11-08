/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Questionnaires 2
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Anton Dmitrijev
 */
package eionet.webq.converter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.Conversion;
import eionet.webq.dto.FileInfo;
import eionet.webq.service.ConversionService;

public class UserFileToFileInfoConverterTest {

    @InjectMocks
    private UserFileToFileInfoConverter fileInfoConverter;

    @Mock
    private ConversionService conversionService;

    private static final int FILE_ID = 1;

    private static final Date NOW = new Date();

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFileInfoWithData() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(FILE_ID);
        userFile.setContent("0123456789".getBytes());
        userFile.setCreated(NOW);
        userFile.setUpdated(NOW);
        userFile.setDownloaded(NOW);
        userFile.setFromCdr(true);

        when(conversionService.conversionsFor(userFile.getXmlSchema())).thenReturn(null);

        FileInfo fileInfo = fileInfoConverter.convert(userFile);
        assertThat(fileInfo.getDownloadLink(), endsWith("fileId=" + FILE_ID));
        assertThat(fileInfo.getConversionLink(), containsString("fileId=" + FILE_ID));
        assertThat(fileInfo.getDeleteLink(), containsString("=" + FILE_ID));
        assertThat(fileInfo.getSize(), equalTo("10 bytes"));
        assertThat(fileInfo.isLocalFile(), equalTo(false));
        assertThat(fileInfo.getCreated(), notNullValue());
        assertThat(fileInfo.getDownloaded(), notNullValue());
        assertThat(fileInfo.getUpdated(), notNullValue());
        assertThat(fileInfo.getConversions(), nullValue());

        verify(conversionService).conversionsFor(userFile.getXmlSchema());

    }

    @Test
    public void getEmptyFileInfo() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(FILE_ID);
        when(conversionService.conversionsFor(userFile.getXmlSchema())).thenReturn(null);

        FileInfo fileInfo = fileInfoConverter.convert(userFile);
        assertThat(fileInfo.getCreated(), nullValue());
        assertThat(fileInfo.getDownloaded(), nullValue());
        assertThat(fileInfo.getSize(), equalTo("0 bytes"));
        assertThat(fileInfo.getConversions(), nullValue());

        verify(conversionService).conversionsFor(userFile.getXmlSchema());
    }

    @Test
    public void callConversionServiceIsExecuted() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(FILE_ID);
        userFile.setAvailableConversions(null);

        List<Conversion> conversions = new ArrayList<Conversion>();
        conversions.add(new Conversion());
        conversions.add(new Conversion());

        when(conversionService.conversionsFor(userFile.getXmlSchema())).thenReturn(conversions);
        FileInfo fileInfo = fileInfoConverter.convert(userFile);
        assertThat(fileInfo.getConversions().size(), equalTo(2));
        verify(conversionService).conversionsFor(userFile.getXmlSchema());
    }

    @Test
    public void callConversionServiceIsNotExecuted() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(FILE_ID);

        List<Conversion> conversions = new ArrayList<Conversion>();
        conversions.add(new Conversion());
        conversions.add(new Conversion());
        userFile.setAvailableConversions(conversions);

        FileInfo fileInfo = fileInfoConverter.convert(userFile);
        assertThat(fileInfo.getConversions().size(), equalTo(2));

        verifyZeroInteractions(conversionService);
    }
}
