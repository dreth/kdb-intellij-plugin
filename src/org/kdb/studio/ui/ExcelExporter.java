package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.type.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class ExcelExporter {

    private static synchronized String sd(String s, Instant x) {
        return DateTimeFormatter.ofPattern(s).withZone(ZoneId.of("UTC")).format(x);
    }

    public static String escape(String s) {
        final StringBuffer result = new StringBuffer();
        final StringCharacterIterator iterator = new StringCharacterIterator(s);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&apos;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    public void exportTableX(final ProgressIndicator progressIndicator, final JTable table, final File file, final boolean openIt) throws IOException {
        TableModel model = table.getModel();

        //"Exporting data to " + file.getAbsolutePath();
        progressIndicator.setText("0% complete");
        progressIndicator.setFraction(0.);

        Writer writer = new BufferedWriter(new PrintWriter(new FileOutputStream(file)));
        writer.write("<?xml version=\"1.0\"?>\n<ss:Workbook xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n");
        writer.write("<ss:Styles>");
        writer.write("<ss:Style ss:ID=\"Default\" ss:Name=\"Normal\">");
        writer.write("<ss:Alignment ss:Vertical=\"Bottom\"/>");
        writer.write("<ss:Borders/>");
        writer.write("<ss:Font/>");
        writer.write("<ss:Interior/>");
        writer.write("<ss:NumberFormat/>");
        writer.write("<ss:Protection/>");
        writer.write("</ss:Style>");
        writer.write("<ss:Style ss:ID=\"bold\"><ss:Font ss:Bold=\"1\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"time\"><ss:NumberFormat ss:Format=\"hh:mm:ss.000\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"minute\"><ss:NumberFormat ss:Format=\"hh:mm\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"month\"><ss:NumberFormat ss:Format=\"yyyy\\-mm\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"second\"><ss:NumberFormat ss:Format=\"hh:mm:ss\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"date\"><ss:NumberFormat ss:Format=\"yyyy\\-mm\\-dd\"/></ss:Style>");
        writer.write("<ss:Style ss:ID=\"datetime\"><ss:NumberFormat ss:Format=\"yyyy\\-mm\\-dd hh:mm:ss.000\"/></ss:Style>");
        writer.write("</ss:Styles>");

        writer.write("<ss:Worksheet ss:Name=\"Sheet1\">\n<ss:Table>\n");
        for (int i = 0; i < model.getColumnCount(); i++) {
            writer.write("<ss:Column ss:Width=\"80\"/>");
        }

        writer.write("\n<ss:Row>");
        for (int i = 0; i < model.getColumnCount(); i++) {
            writer.write("<ss:Cell><ss:Data ss:Type=\"String\">");
            writer.write(escape(model.getColumnName(i)));
            writer.write("</ss:Data></ss:Cell>");
        }
        writer.write("</ss:Row>\n");

        int maxRow = model.getRowCount();
        for (int i = 0; i < model.getRowCount(); i++) {
            writer.write("<ss:Row>");

            for (int j = 0; j < model.getColumnCount(); j++) {

                KBase b = (KBase) model.getValueAt(i, j);
                if (!b.isNull()) {
                    if (table.getColumnClass(j) == KSymbolVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"String\">" + escape(b.toString(false)));
                    } else if (table.getColumnClass(j) == KDateVector.class) {
                        writer.write("<ss:Cell ss:StyleID=\"date\"><ss:Data ss:Type=\"DateTime\">" +
                                sd("yyyy-MM-dd", ((KDate) b).toDate()));
                    } else if (table.getColumnClass(j) == KTimeVector.class) {
                        writer.write("<ss:Cell ss:StyleID=\"time\"><ss:Data ss:Type=\"DateTime\">" +
                                "1899-12-31T" + sd("HH:mm:ss.SSS", ((KTime) b).toTime()));
                    } else if (table.getColumnClass(j) == KTimestampVector.class) {
                        char[] cs = sd("yyyy-MM-dd HH:mm:ss.SSS", Instant.class.cast(((KTimestamp) b).toTimestamp()[0])).toCharArray();
                        cs[10] = 'T';
                        writer.write("<ss:Cell ss:StyleID=\"datetime\"><ss:Data ss:Type=\"DateTime\">" + new String(cs));
                    } else if (table.getColumnClass(j) == KMonthVector.class) {
                        writer.write("<ss:Cell ss:StyleID=\"month\"><ss:Data ss:Type=\"DateTime\">" + sd("yyyy-MM", ((Month) b).toDate()));
                    } else if (table.getColumnClass(j) == KMinuteVector.class) {
                        writer.write("<ss:Cell ss:StyleID=\"minute\"><ss:Data ss:Type=\"DateTime\">" +
                                "1899-12-31T" + sd("HH:mm", ((Minute) b).toDate()));
                    } else if (table.getColumnClass(j) == KSecondVector.class) {
                        writer.write("<ss:Cell ss:StyleID=\"second\"><ss:Data ss:Type=\"DateTime\">" +
                                "1899-12-31T" + sd("HH:mm:ss", ((Second) b).toDate()));
                    } else if (table.getColumnClass(j) == KBooleanVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Boolean\">" + (((KBoolean) b).b ? "1" : "0"));
                    } else if (table.getColumnClass(j) == KDoubleVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Number\">" + ((KDouble) b).d);
                    } else if (table.getColumnClass(j) == KFloatVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Number\">" + ((KFloat) b).f);
                    } else if (table.getColumnClass(j) == KLongVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Number\">" + ((KLong) b).j);
                    } else if (table.getColumnClass(j) == KIntVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Number\">" + ((KInteger) b).i);
                    } else if (table.getColumnClass(j) == KShortVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"Number\">" + ((KShort) b).s);
                    } else if (table.getColumnClass(j) == KCharacterVector.class) {
                        writer.write("<ss:Cell><ss:Data ss:Type=\"String\">" + escape(new String(new char[]{((KCharacter) b).c})));
                    } else {
                        LimitedWriter w = new LimitedWriter(20000);
                        try {
                            b.toString(w, false);
                        } catch (LimitedWriter.LimitException e) {
                            Notifications.Bus.notify(new Notification("KDBStudio", "Failed to export full data to excel","Data is loo long. Cut output.", NotificationType.WARNING));
                        } catch (Exception e) {
                            Notifications.Bus.notify(new Notification("KDBStudio", "There was an error exporting to excel.", e.getMessage(), NotificationType.ERROR));
                        }
                        writer.write("<ss:Cell><ss:Data ss:Type=\"String\">" + escape(w.toString()));
                    }
                } else {
                    writer.write("<ss:Cell><ss:Data ss:Type=\"String\">");
                }

                writer.write("</ss:Data></ss:Cell>");
            }
            progressIndicator.checkCanceled();
            progressIndicator.setFraction(i / maxRow);
            progressIndicator.setText((i * 100) / maxRow + "% complete");
            writer.write("</ss:Row>\n");
        }

        writer.write("</ss:Table>\n</ss:Worksheet>\n</ss:Workbook>");
        writer.close();

        progressIndicator.checkCanceled();
        if (openIt) {
            openTable(file);
        }
    }


    public void openTable(File file) {
        try {
            Runtime run = Runtime.getRuntime();
            String lcOSName = System.getProperty("os.name").toLowerCase();
            boolean MAC_OS_X = lcOSName.startsWith("mac os x");
            Process p = null;
            if (MAC_OS_X) {
                p = run.exec("open " + file);
            } else {
                run.exec("cmd.exe /c start " + file);
            }
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification("KDBStudio", "There was an error opening excel.", "Perhaps you do not have Excel installed, or .xls files are not associated with Excel", NotificationType.ERROR));
        }
    }
}
