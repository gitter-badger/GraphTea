// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphtea.plugins.reports.ui;

import graphtea.platform.core.BlackBoard;
import graphtea.platform.parameter.Parametrizable;
import graphtea.plugins.main.GraphData;
import graphtea.plugins.reports.extension.GraphReportExtension;
import graphtea.ui.AttributeSetView;
import graphtea.ui.ParameterShower;
import graphtea.ui.PortableNotifiableAttributeSetImpl;
import graphtea.ui.components.gpropertyeditor.GPropertyEditor;
import graphtea.ui.components.gpropertyeditor.GPropertyTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author azin azadi
 */
public class ReportsUI {
    JLabel info = new JLabel("Click on a report to calculate it");
    public GPropertyEditor propEd = new GPropertyEditor();
    GraphData graphData;
    public ArrayList<GraphReportExtension> reports = new ArrayList<GraphReportExtension>();
    public HashMap<String, GraphReportExtension> reportByName = new HashMap<String, GraphReportExtension>();

    public PortableNotifiableAttributeSetImpl reportResults = new PortableNotifiableAttributeSetImpl();

    JFrame frm = new JFrame("Reports");
    public static ReportsUI self = null;

    public ReportsUI(BlackBoard b, boolean init) {
        super();
        if (init)
            initComponents();
        propEd.connect(reportResults);
        self = this;
        BlackBoard blackboard = b;
        graphData = new GraphData(blackboard);
    }

    private void initComponents() {
        BorderLayout lom = new BorderLayout(0, 0);
        frm.setLayout(lom);
        frm.add(initWrapper());
        frm.setPreferredSize(new Dimension(150,100));
        frm.pack();
    }

    public void initTable() {
        for (GraphReportExtension gre : reports) {
            String name = gre.getName();
            reportResults.put(name, "Click to Calculate");
            reportByName.put(name, gre);
            AttributeSetView view = reportResults.getView();
            view.setEditable(name, false);
            view.setDescription(name, gre.getDescription());
        }
        propEd.connect(reportResults);
        propEd.getTable().addNotify();
        propEd.getTable().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = propEd.getTable().rowAtPoint(e.getPoint());
                GPropertyTableModel gtm = (GPropertyTableModel) propEd.getTable().getModel();
                ParameterShower ps = new ParameterShower();
                String name = (String) gtm.getValueAt(row, 0);
                GraphReportExtension o = reportByName.get(name);
                if (o instanceof Parametrizable){
                    if (ps.xshow(o)) {
                        reCalculateReport(name);
                    }
                }
                else{
                    reCalculateReport(name);
                }

            }
        });

    }

    JPanel initWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        JScrollPane jsp = new JScrollPane(propEd);
        propEd.setPreferredSize(new Dimension(150,100));
        wrapper.setPreferredSize(new Dimension(150, 100));
        wrapper.add(jsp, BorderLayout.CENTER);
        wrapper.add(info, BorderLayout.NORTH);
        return wrapper;
    }
//    public Component getComponent(blackboard b) {
//        return ;
//    }

    public void addReport(GraphReportExtension gre) {
        reports.add(gre);
//        reCalculateReports();
    }

    public void show() {
        frm.setVisible(true);
    }

    public void hide() {
        frm.setVisible(false);
    }

    public void reCalculateReports() {
        if (graphData.getGraph() == null)
            return;
        for (GraphReportExtension gre : reports) {
            String name = gre.getName();
            reportResults.put(name, gre.calculate(graphData));
            reportByName.put(name, gre);
            AttributeSetView view = reportResults.getView();
            view.setEditable(name, false);
            view.setDescription(name, gre.getDescription());
        }
        propEd.connect(reportResults);
    }

    public void reCalculateReport(String reportName) {
        GraphReportExtension gre = reportByName.get(reportName);
        reportResults.put(reportName, gre.calculate(graphData));
    }
}