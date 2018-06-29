package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.kx.type.KBase;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import java.io.*;

public class FileExporter {

    FileFilter csvFilter =
            new FileFilter() {
                public String getDescription() {
                    return "csv (Comma delimited)";
                }

                public boolean accept(File file) {
                    if (file.isDirectory() || file.getName().endsWith(".csv"))
                        return true;
                    else
                        return false;
                }
            };

    FileFilter txtFilter =
            new FileFilter() {
                public String getDescription() {
                    return "txt (Tab delimited)";
                }

                public boolean accept(File file) {
                    if (file.isDirectory() || file.getName().endsWith(".txt"))
                        return true;
                    else
                        return false;
                }
            };

    FileFilter xmlFilter =
            new FileFilter() {
                public String getDescription() {
                    return "xml";
                }

                public boolean accept(File file) {
                    if (file.isDirectory() || file.getName().endsWith(".xml"))
                        return true;
                    else
                        return false;
                }
            };


    FileFilter xlsFilter =
            new FileFilter() {
                public String getDescription() {
                    return "xls (Microsoft Excel)";
                }

                public boolean accept(File file) {
                    if (file.isDirectory() || file.getName().endsWith(".xls"))
                        return true;
                    else
                        return false;
                }
            };

    private String lastFileName;

    public FileExporter() {

    }


    public void exportTable(Project project, JTable table) {

        String defaultFileName = lastFileName;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Export result set as");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.addChoosableFileFilter(csvFilter);
        chooser.addChoosableFileFilter(txtFilter);
        chooser.addChoosableFileFilter(xmlFilter);
        chooser.addChoosableFileFilter(xlsFilter);

        if (defaultFileName != null) {
            File file = new File(defaultFileName);
            File dir = new File(file.getPath());
            chooser.setCurrentDirectory(dir);
            chooser.ensureFileIsVisible(file);
            if (defaultFileName.endsWith(".xls"))
                chooser.setFileFilter(xlsFilter);
            else if (defaultFileName.endsWith(".csv"))
                chooser.setFileFilter(csvFilter);
            else if (defaultFileName.endsWith(".xml"))
                chooser.setFileFilter(xmlFilter);
            else if (defaultFileName.endsWith(".txt"))
                chooser.setFileFilter(txtFilter);
        }

        int option = chooser.showSaveDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File sf = chooser.getSelectedFile();
            File f = chooser.getCurrentDirectory();
            String dir = f.getAbsolutePath();
            try {

                FileFilter ff = chooser.getFileFilter();

                StringBuilder exportFilename = new StringBuilder(dir).append(File.separator).append(sf.getName());
                if (!exportFilename.toString().endsWith(".xls") && ff == xlsFilter) {
                    exportFilename.append(".xls");
                } else if (!exportFilename.toString().endsWith(".csv") && ff == csvFilter) {
                    exportFilename.append(".csv");
                } else if (!exportFilename.toString().endsWith(".txt") && ff == txtFilter) {
                    exportFilename.append(".txt");
                } else if (!exportFilename.toString().endsWith(".xml") && ff == xmlFilter) {
                    exportFilename.append(".xml");
                }

                String name = exportFilename.toString();
                if(new File(name).exists()) {

                    ConfirmDialog dialog = new ConfirmDialog(project, "File " + new File(name).getName() + " already exists. Overwrite?");
                    dialog.show();
                    if (DialogWrapper.OK_EXIT_CODE == dialog.getExitCode()) {
                        doExport(name, project, table);
                    }
                } else {
                    doExport(name, project, table);
                }
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Data export error.", e.getMessage(), NotificationType.ERROR));
            }
        }
    }

    private void doExport(String name, Project project, JTable table) {
        if (name.endsWith(".xls"))
            exportAsExcel(name, project, table);
        else if (name.endsWith(".csv"))
            exportAsCSV(name, project, table);
        else if (name.endsWith(".txt"))
            exportAsTxt(name, project, table);
        else if (name.endsWith(".xml"))
            exportAsXml(name, project, table);
        else {
            Notifications.Bus.notify(new Notification("KDBStudio", "Data export error.", "You did not specify what format to export the file. Cancelling data export", NotificationType.ERROR));
        }
    }

    private void exportAsExcel(final String filename, Project project, JTable table) {
        lastFileName = filename;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Exporting data to " + filename, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    new ExcelExporter().exportTableX(progressIndicator, table, new File(filename), false);
                    confirm();
                } catch (IOException e) {
                    Notifications.Bus.notify(new Notification("KDBStudio", "There was an error converting to excel.", e.getMessage(), NotificationType.ERROR));
                }
            }
        });
    }

    private void exportAsTxt(final String filename, Project project, JTable table) {
        lastFileName = filename;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Exporting data to " + filename, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                exportAsDelimited(progressIndicator, table.getModel(), filename, '\t');
            }
        });
    }

    private void exportAsCSV(final String filename, Project project, JTable table) {
        lastFileName = filename;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Exporting data to " + filename, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                exportAsDelimited(progressIndicator, table.getModel(), filename, ',');
            }
        });
    }

    private void exportAsXml(final String filename, Project project, JTable table) {
        lastFileName = filename;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Exporting data to " + filename, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                exportAsXml(progressIndicator, table.getModel(), filename);
            }
        });
    }

    private void exportAsXml(ProgressIndicator progressIndicator, final TableModel model,final String filename) {
        progressIndicator.setText("0% complete");
        progressIndicator.setFraction(0.);

        if (filename != null) {
            String lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

            BufferedWriter fw = null;

            try {
                fw = new BufferedWriter(new FileWriter(filename));

                fw.write("<R>");

                int maxRow = model.getRowCount();
                fw.write(lineSeparator);
                String[] columns = new String[model.getColumnCount()];
                for (int col = 0;col < model.getColumnCount();col++)
                    columns[col] = model.getColumnName(col);

                for (int r = 1;r <= maxRow;r++) {
                    fw.write("<r>");
                    for (int col = 0;col < columns.length;col++) {
                        fw.write("<" + columns[col] + ">");

                        KBase o = (KBase) model.getValueAt(r - 1,col);
                        if (!o.isNull())
                            fw.write(o.toString(false));

                        fw.write("</" + columns[col] + ">");
                    }
                    fw.write("</r>");
                    fw.write(lineSeparator);

                    progressIndicator.checkCanceled();
                    progressIndicator.setText("" + (100 * r) / maxRow + "% complete");
                    progressIndicator.setFraction(r / maxRow);
                }
                fw.write("</R>");

                fw.close();
                confirm();
            } catch (Exception ex) {
                Notifications.Bus.notify(new Notification("KDBStudio", "There was an error while export.", ex.getMessage(), NotificationType.ERROR));
            }
        }
    }

    private void exportAsDelimited(ProgressIndicator progressIndicator, final TableModel model, final String filename, final char delimiter) {
        String lineSeparator = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

        BufferedWriter fw;

        try {
            fw = new BufferedWriter(new FileWriter(filename));

            for (int col = 0; col < model.getColumnCount(); col++) {
                if (col > 0)
                    fw.write(delimiter);

                fw.write(model.getColumnName(col));
            }
            fw.write(lineSeparator);

            int maxRow = model.getRowCount();
            for (int r = 1; r <= maxRow; r++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    if (col > 0)
                        fw.write(delimiter);

                    KBase o = (KBase) model.getValueAt(r - 1, col);
                    if (!o.isNull())
                        fw.write(o.toString(false));
                }
                fw.write(lineSeparator);

                progressIndicator.checkCanceled();
                progressIndicator.setText("" + (100 * r) / maxRow + "% complete");
                progressIndicator.setFraction(r / maxRow);
            }

            fw.close();
            confirm();
        } catch (Exception ex) {
            Notifications.Bus.notify(new Notification("KDBStudio", "There was an error while export.", ex.getMessage(), NotificationType.ERROR));
        }

    }

    private void confirm() {
        Notifications.Bus.notify(new Notification("KDBStudio", "Export complete", "Exported to " + lastFileName, NotificationType.INFORMATION));
    }
}
