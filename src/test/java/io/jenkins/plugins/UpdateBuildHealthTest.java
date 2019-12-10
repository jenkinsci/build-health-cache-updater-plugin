/*
 * The MIT License
 *
 * Copyright (c) 2019, CloudBees Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.plugins;

import hudson.model.FreeStyleProject;
import hudson.model.HealthReport;
import hudson.model.Result;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SleepBuilder;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UpdateBuildHealthTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void normal() throws Exception {
        final FreeStyleProject one = j.createFreeStyleProject("one");
        one.getBuildersList().add(new SleepBuilder(3));
        assertNull(Whitebox.getInternalState(one, "cachedBuildHealthReportsBuildNumber"));
        j.buildAndAssertSuccess(one);
        Integer cachedBuildHealthReportsBuildNumber = Whitebox.getInternalState(one, "cachedBuildHealthReportsBuildNumber");
        assertNotNull(cachedBuildHealthReportsBuildNumber);
        assertEquals(1, cachedBuildHealthReportsBuildNumber.intValue());

        final FreeStyleProject two = j.createFreeStyleProject("two");
        two.getBuildersList().add(new SleepBuilder(3));
        two.getBuildersList().add(new FailureBuilder());
        j.buildAndAssertStatus(Result.FAILURE, two);
        cachedBuildHealthReportsBuildNumber = Whitebox.getInternalState(two, "cachedBuildHealthReportsBuildNumber");
        assertNotNull(cachedBuildHealthReportsBuildNumber);
        assertEquals(1, cachedBuildHealthReportsBuildNumber.intValue());

        j.buildAndAssertSuccess(one);
        j.buildAndAssertStatus(Result.FAILURE, two);
        j.buildAndAssertSuccess(one);
        j.buildAndAssertStatus(Result.FAILURE, two);

        cachedBuildHealthReportsBuildNumber = Whitebox.getInternalState(one, "cachedBuildHealthReportsBuildNumber");
        assertNotNull(cachedBuildHealthReportsBuildNumber);
        assertEquals(3, cachedBuildHealthReportsBuildNumber.intValue());

        cachedBuildHealthReportsBuildNumber = Whitebox.getInternalState(two, "cachedBuildHealthReportsBuildNumber");
        assertNotNull(cachedBuildHealthReportsBuildNumber);
        assertEquals(3, cachedBuildHealthReportsBuildNumber.intValue());


        List<HealthReport> cachedBuildHealthReports = Whitebox.getInternalState(one, "cachedBuildHealthReports");
        assertNotNull(cachedBuildHealthReports);
        assertFalse(cachedBuildHealthReports.isEmpty());
        assertEquals(100, cachedBuildHealthReports.get(0).getScore());

        cachedBuildHealthReports = Whitebox.getInternalState(two, "cachedBuildHealthReports");
        assertNotNull(cachedBuildHealthReports);
        assertFalse(cachedBuildHealthReports.isEmpty());
        assertEquals(0, cachedBuildHealthReports.get(0).getScore());

    }
}
