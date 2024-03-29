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

import hudson.Extension;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class UpdateBuildHealth extends RunListener<Run<?,?>> {

    private static final Logger logger = Logger.getLogger(UpdateBuildHealth.class.getName());

    @Override
    public void onFinalized(Run<?,?> run) {
        long start = 0;
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Calculating BuildHealthReports for {0}", run.getExternalizableId());
            start = System.currentTimeMillis();
        }
        run.getParent().getBuildHealthReports();

        if (logger.isLoggable(Level.FINE)) {
            long time = System.currentTimeMillis() - start;
            Object[] params = new Object[]{run.getExternalizableId(), TimeUnit.MILLISECONDS.toSeconds(time)};
            logger.log(Level.FINE, "BuildHealthReports for {0} done. Time: {1}s", params);
        }
    }
}
