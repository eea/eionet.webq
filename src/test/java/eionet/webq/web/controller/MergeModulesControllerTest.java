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
package eionet.webq.web.controller;

import eionet.webq.dao.MergeModules;
import eionet.webq.dao.orm.MergeModule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class MergeModulesControllerTest {
    @InjectMocks
    private MergeModulesController controller;
    @Mock
    private MergeModules modulesStorage;
    @Mock
    private Model model;
    @Mock
    private BindingResult bindingResult;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenListingAllModules_loadsAllModulesToModel() throws Exception {
        List<MergeModule> modules = Collections.emptyList();
        when(modulesStorage.findAll()).thenReturn(modules);

        controller.listMergeModules(model);

        verify(modulesStorage).findAll();
        verify(model).addAttribute("allMergeModules", modules);
    }

    @Test
    public void whenAddingNewModule_emptyModuleShouldBeAddedToModel() throws Exception {
        controller.addModule(model);
        verify(model).addAttribute(eq("mergeModule"), any(MergeModule.class));
    }

    @Test
    public void whenSavingModule_andIdNotSet_performSaveToStorage() throws Exception {
        MergeModule module = new MergeModule();
        controller.save(module, bindingResult, model);

        verify(modulesStorage).save(module);
    }

    @Test
    public void whenSaving_ifBindingResultHasErrors_returnBackToAddPage() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        String viewName = controller.save(new MergeModule(), bindingResult, model);

        assertThat(viewName, equalTo("add_edit_merge_module"));
    }

    @Test
    public void removesModulesById() throws Exception {
        int[] modulesToRemove = {1, 2, 3};
        controller.remove(modulesToRemove, model);

        verify(modulesStorage).remove(modulesToRemove);
    }

    @Test
    public void whenSavingModule_ifIdSetToValueGreaterThanZero_performUpdateInStorage() throws Exception {
        MergeModule mergeModule = new MergeModule();
        mergeModule.setId(1);
        String viewName = controller.save(mergeModule, bindingResult, model);
        verify(modulesStorage).update(mergeModule);
        assertThat(viewName, equalTo("merge_modules"));
    }
}
