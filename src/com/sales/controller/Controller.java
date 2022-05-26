
package com.sales.controller;

import com.sales.model.Invoice;
import com.sales.model.InvoicesTableModel;
import com.sales.model.Line;
import com.sales.model.LinesTableModel;
import com.sales.view.InvoiceDialog;
import com.sales.view.InvoiceFrame;
import com.sales.view.LineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Controller implements ActionListener, ListSelectionListener  {
    
    private InvoiceFrame frame;
    private InvoiceDialog invoiceDialog;
    private LineDialog lineDialog;
    public Controller(InvoiceFrame frame){
          this.frame = frame;  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        switch (actionCommand){
            case "Load File":
                loadFile();
                break;
                
            case "Save File":
                saveFile();
                break;    
                
            case "Create New Invoice":
                createNewInvoice();
                break;   
                
            case "Delete Invoice":
                deleteInvoice();
                break;   
                
            case "Create New Item":
                createNewItem();
                break;  
                
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineOK":
                createLineOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
        }
    }
        @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getInvoiceTable().getSelectedRow();
        if(selectedIndex != -1){
        System.out.println("The user has selected row :" + selectedIndex );
        Invoice currentInvoice = frame.getInvoices().get(selectedIndex);
        frame.getInvoiceNumLabel().setText("" + currentInvoice.getNum());
        frame.getInvoiceDateLabel().setText(currentInvoice.getDate());
        frame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
        frame.getInvoiceTotalLabel().setText("" + currentInvoice.getInvoiceTotal());
        LinesTableModel linesTableModel = new LinesTableModel(currentInvoice.getLines());
        frame.getLineTable().setModel(linesTableModel);
        linesTableModel.fireTableDataChanged();
        }
        
    }
    
    private void loadFile() {
        JFileChooser fc = new JFileChooser ();
        try {
        int outcome = fc.showOpenDialog(frame);
        if (outcome == JFileChooser.APPROVE_OPTION){
            File headerFile = fc.getSelectedFile();
            Path headerPath = Paths.get(headerFile.getAbsolutePath());
            List<String> headerLines = Files.readAllLines(headerPath);
            System.out.println("The invoices have been read successfully");
            
            ArrayList<Invoice> invoicesArray = new ArrayList<>();
            for (String headerLine : headerLines){
                try{
                String [] headerParts = headerLine.split(",");
                int invoiceNum = Integer.parseInt(headerParts[0]);
                String invoiceDate = headerParts[1];
                String customerName = headerParts[2];
                
                Invoice invoice = new Invoice (invoiceNum,invoiceDate,customerName);
                invoicesArray.add(invoice);
                } catch (Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
            System.out.println("make a check");
            outcome = fc.showOpenDialog(frame);
            if (outcome == JFileChooser.APPROVE_OPTION){
                File lineFile = fc.getSelectedFile();
                Path linePath = Paths.get(lineFile.getAbsolutePath());
                List<String> lineLines = Files.readAllLines(linePath);
                System.out.println("The lines have been read successfully");
                for(String lineLine : lineLines){
                    try{
                    String lineParts[] = lineLine.split(",");
                    int invoiceNum = Integer.parseInt(lineParts[0]);
                    String itemName = lineParts[1];
                    double itemPrice = Double.parseDouble(lineParts[2]);
                    int count = Integer.parseInt(lineParts[3]);
                    Invoice inv = null;
                    for (Invoice invoice : invoicesArray){
                        if(invoice.getNum() == invoiceNum){
                            inv = invoice;
                            break;
                        }
                    }
                    Line line = new Line (itemName, itemPrice,count, inv);
                    inv.getLines().add(line);
                    }catch (Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                }
                }
                System.out.println("make a check");
            }
            
           frame.setInvoices(invoicesArray);
           InvoicesTableModel invoicesTableModel = new InvoicesTableModel(invoicesArray);
           frame.setInvoicesTableModel(invoicesTableModel);
           frame.getInvoiceTable().setModel(invoicesTableModel);
           frame.getInvoicesTableModel().fireTableDataChanged();
            }
        } catch (IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Can't read file", "Error", JOptionPane.ERROR_MESSAGE);

        }
        
}

    private void saveFile() {
        ArrayList<Invoice> invoices = frame.getInvoices();
        String headers = "";
        String lines = "";
        for (Invoice invoice : invoices) {
            String invCSV = invoice.getAsCSV();
            headers += invCSV;
            headers += "\n";

            for (Line line : invoice.getLines()) {
                String lineCSV = line.getAsCSV();
                lines += lineCSV;
                lines += "\n";
            }
        }
        System.out.println("Make a check");
        try {
            JFileChooser fc = new JFileChooser();
            int outcome = fc.showSaveDialog(frame);
            if (outcome == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter hfw = new FileWriter(headerFile);
                hfw.write(headers);
                hfw.flush();
                hfw.close();
                outcome = fc.showSaveDialog(frame);
                if (outcome == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lfw = new FileWriter(lineFile);
                    lfw.write(lines);
                    lfw.flush();
                    lfw.close();
                }
            }
        } catch (Exception ex) {

        }
}

    private void createNewInvoice() {
        invoiceDialog = new InvoiceDialog(frame);
        invoiceDialog.setVisible(true);       
}

    private void deleteInvoice() {
        int selectedRow = frame.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1 ){
            frame.getInvoices().remove(selectedRow);
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
}

    private void createNewItem() {
        lineDialog = new LineDialog(frame);
        lineDialog.setVisible(true);

}

    private void deleteItem() {
        int selectedInv = frame.getInvoiceTable().getSelectedRow();
        int selectedRow = frame.getLineTable().getSelectedRow();
        
        if (selectedInv != -1 && selectedRow != -1 ){
            Invoice invoice = frame.getInvoices().get(selectedInv);
            invoice.getLines().remove(selectedRow);
            LinesTableModel linesTableModel = new LinesTableModel(invoice.getLines());
            frame.getLineTable().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();

        }
}

    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void createInvoiceOK() {
            String date = invoiceDialog.getInvDateField().getText();
        String customer = invoiceDialog.getCustNameField().getText();
        int num = frame.getNextInvoiceNum();
        try {
            String[] dateParts = date.split("-");  // "22-05-2013" -> {"22", "05", "2013"}  xy-qw-20ij
            if (dateParts.length < 3) {
                JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                if (day > 31 || month > 12) {
                    JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Invoice invoice = new Invoice(num, date, customer);
                    frame.getInvoices().add(invoice);
                    frame.getInvoicesTableModel().fireTableDataChanged();
                    invoiceDialog.setVisible(false);
                    invoiceDialog.dispose();
                    invoiceDialog = null;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
   
    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countString = lineDialog.getItemCountField().getText();
        String priceString = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countString);
        double price = Double.parseDouble(priceString);
        int selectedInvoice = frame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            Invoice invoice = frame.getInvoices().get(selectedInvoice);
            Line line = new Line(item, price, count, invoice);
            invoice.getLines().add(line);
            LinesTableModel linesTableModel = (LinesTableModel) frame.getLineTable().getModel();
            linesTableModel.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

    private void createLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }
}





    
    

