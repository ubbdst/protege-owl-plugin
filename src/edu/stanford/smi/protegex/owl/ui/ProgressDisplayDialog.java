/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.ui;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protegex.owl.model.Task;
import edu.stanford.smi.protegex.owl.model.TaskProgressDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProgressDisplayDialog extends JDialog implements TaskProgressDisplay {

    private static final long serialVersionUID = -3732900819838480521L;

    private static final int DISPLAY_DELAY = 3000;

    private Box box;

    private Map taskProgressPanelMap;

    private NullTaskDisplay nullTaskDisplay;

    private Map subTaskTimers;

    public ProgressDisplayDialog() {
        super(getOwnerFrame(), true);
        taskProgressPanelMap = new HashMap();
        nullTaskDisplay = new NullTaskDisplay();
        subTaskTimers = new HashMap();
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        createUI();
    }

    private static Frame getOwnerFrame() {
        Project project = ProjectManager.getProjectManager().getCurrentProject();
        if(project != null) {
            Component c = ProtegeUI.getTopLevelContainer(project);
            if(c instanceof Frame) {
                return (Frame)c;
            }
        }
        return null;
    }


    private void createUI() {
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        JLabel iconLabel = new JLabel(UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        JPanel iconLabelHolder = new JPanel(new BorderLayout());
        iconLabelHolder.add(iconLabel, BorderLayout.NORTH);
        panel.add(iconLabelHolder, BorderLayout.WEST);
        box = new Box(BoxLayout.Y_AXIS);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(box, BorderLayout.CENTER);
        setContentPane(panel);
        pack();
    }


    public void run(final Task task) throws Exception {
        final ProgressPanel pp = new ProgressPanel(task);
        taskProgressPanelMap.put(task, pp);
        Runnable runnable = new Runnable() {
            public void run() {
                if (taskProgressPanelMap.size() > 1) {
                    final Timer t = new Timer(DISPLAY_DELAY, new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            box.add(pp);
                            pack();
                            subTaskTimers.remove(task);
                        }

                    });
                    t.setRepeats(false);
                    t.start();
                    subTaskTimers.put(task, t);
                }
                else {
                    box.add(pp);
                    pack();
                }
            }
        };
        if(SwingUtilities.isEventDispatchThread() == false) {
            SwingUtilities.invokeLater(runnable);
        }
        else {
            runnable.run();
        }
        runTask(task);
    }

    private void runTask(final Task task) throws Exception {
        // Make sure that the task is not run in the
        // event dispatch thread - we don't want to
        // block the UI
        if(SwingUtilities.isEventDispatchThread()) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        task.runTask();
                        end(task);
                    }
                    catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t.start();
            // Called from event dispatch thread
            displayProgressDialog();
        }
        else {
            task.runTask();
            end(task);
        }

    }

    private void displayProgressDialog() {
        if(SwingUtilities.isEventDispatchThread() == false) {
            throw new IllegalStateException("[ProgressDisplayDialog] displayOrBlock should only be called from the event dispatch thread!");
        }
        long time = System.currentTimeMillis();
        while(System.currentTimeMillis() - time < DISPLAY_DELAY) {
            // Block thread
        }
        // Show the dialog if the task is still running.
        if(taskProgressPanelMap.size() > 0) {
            centreDialog();
            setVisible(true);
        }
    }

    private ProgressPanel getProgressPanel(Task task) {
        ProgressPanel pp = (ProgressPanel) taskProgressPanelMap.get(task);
        return pp;
    }

    private TaskDisplay getTaskDisplay(Task task) {
        TaskDisplay display = getProgressPanel(task);
        if(display == null) {
            display = nullTaskDisplay;
        }
        return display;
    }


    public void setProgress(final Task task, final int progress) {
        Runnable runnable = new Runnable() {
            public void run() {
                getTaskDisplay(task).setProgress(progress);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    public void setProgressIndeterminate(final Task task, final boolean b) {
        Runnable runnable = new Runnable() {
            public void run() {
                getTaskDisplay(task).setProgressIndeterminate(b);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    public void setMessage(final Task task, final String message) {
        Runnable runnable = new Runnable() {
            public void run() {
                getTaskDisplay(task).setMessage(message);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    public void end(final Task task) {
        Timer timer = (Timer) subTaskTimers.get(task);
        if(timer != null) {
            timer.stop();
        }
        subTaskTimers.remove(task);
        Runnable runnable = new Runnable() {
            public void run() {
                ProgressPanel pp = getProgressPanel(task);
                if(pp != null) {
                    taskProgressPanelMap.remove(task);
                    box.remove(pp);
                    pack();
                }
                if(taskProgressPanelMap.size() == 0) {
                    setVisible(false);
                }
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    private void centreDialog() {
        Dimension size = getToolkit().getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2,
                    size.height / 2 - getHeight() / 2);
    }

    private class ProgressPanel extends JPanel implements TaskDisplay {

        private static final long serialVersionUID = -5710782039645228802L;

        private JLabel titleLabel;

        private JLabel msgLabel;

        private JProgressBar progressBar;

        public static final int INDENT = 20;

        public ProgressPanel(Task task) {
            setLayout(new BorderLayout(7, 7));
            setBorder(BorderFactory.createEmptyBorder(10, taskProgressPanelMap.size() == 0 ? 0 : INDENT, 5, 0));
            JPanel labelPanel = new JPanel(new BorderLayout(3, 3));
            titleLabel = new JLabel(task.getTitle());
            labelPanel.add(titleLabel, BorderLayout.NORTH);
            msgLabel = new JLabel("");
            msgLabel.setFont(msgLabel.getFont().deriveFont(msgLabel.getFont().getSize2D() * 0.8f));
            msgLabel.setBorder(BorderFactory.createEmptyBorder(0, INDENT, 0, 0));
            labelPanel.add(msgLabel, BorderLayout.SOUTH);
            add(labelPanel, BorderLayout.NORTH);
            progressBar = new JProgressBar(task.getProgressMin(), task.getProgressMax());
            progressBar.setIndeterminate(true);
            JPanel progressBarHolder = new JPanel(new BorderLayout(7, 7));
            progressBarHolder.add(progressBar, BorderLayout.NORTH);
            progressBarHolder.setBorder(BorderFactory.createEmptyBorder(0, INDENT, 0, 0));
            add(progressBarHolder, BorderLayout.SOUTH);
        }


        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color oldColor = g.getColor();
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(5, getHeight() - 2, getWidth() - 5, getHeight() - 2);
            g.setColor(oldColor);
        }


        public void setMessage(String message) {
            msgLabel.setText(message);
        }


        public void setProgress(int progress) {
            if(progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(false);
            }
            progressBar.setValue(progress);
        }


        public void setProgressIndeterminate(boolean b) {
            progressBar.setIndeterminate(b);
        }

    }


    private class NullTaskDisplay implements TaskDisplay {

        public void setProgress(int progress) {
        }


        public void setProgressIndeterminate(boolean b) {
        }


        public void setMessage(String message) {
        }
    }


    private interface TaskDisplay {
        public void setProgress(int progress);

        public void setProgressIndeterminate(boolean b);

        public void setMessage(String message);
    }
}

