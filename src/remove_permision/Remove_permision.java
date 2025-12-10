package remove_permision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class Remove_permision extends JFrame {

    private static final long serialVersionUID = 1L;
    private File[] selectedFiles;

    public Remove_permision() {
        setTitle("Quitar Permisos PDF – PDFBox");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 330);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {

        // --- DROPZONE ---
        JPanel dropZone = new JPanel();
        dropZone.setPreferredSize(new Dimension(480, 180));
        dropZone.setBackground(new Color(245, 245, 245));
        dropZone.setBorder(BorderFactory.createDashedBorder(Color.GRAY));

        JLabel label = new JLabel("Arrastra aquí tus archivos PDF");
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dropZone.add(label);

        dropZone.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) support.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    selectedFiles = files.toArray(new File[0]);
                    label.setText("PDFs cargados: " + selectedFiles.length);

                } catch (Exception ex) {
                    label.setText("Error al cargar archivos");
                }
                return true;
            }
        });

        // --- PANEL DE BOTONES INFERIOR ---
        JPanel bottomPanel = new JPanel();

        JButton btnBrowse = new JButton("Seleccionar archivos");
        btnBrowse.setFocusable(false);
        btnBrowse.addActionListener(this::selectFiles);

        JButton processBtn = new JButton("Quitar permisos y guardar");
        processBtn.setFocusable(false);
        processBtn.addActionListener(this::processFiles);

        bottomPanel.add(btnBrowse);
        bottomPanel.add(processBtn);

        add(dropZone, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectFiles(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivos PDF");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();
            JOptionPane.showMessageDialog(this,
                    "Se han cargado " + selectedFiles.length + " PDF(s).",
                    "Archivos cargados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processFiles(ActionEvent e) {
        if (selectedFiles == null || selectedFiles.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No has cargado ningún PDF.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (File f : selectedFiles) {
            if (!f.getName().toLowerCase().endsWith(".pdf"))
                continue;

            // --- PREGUNTAR DONDE GUARDAR ---
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar PDF sin permisos");
            chooser.setSelectedFile(new File(f.getName()));
            chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

            int option = chooser.showSaveDialog(this);

            if (option != JFileChooser.APPROVE_OPTION)
                continue;

            File output = chooser.getSelectedFile();
            if (!output.getName().toLowerCase().endsWith(".pdf")) {
                output = new File(output.getAbsolutePath() + ".pdf");
            }

            try (PDDocument document = Loader.loadPDF(f)) {

                document.setAllSecurityToBeRemoved(true);
                document.save(output);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error procesando " + f.getName() + ":\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        JOptionPane.showMessageDialog(this,
                "Proceso completado.",
                "Listo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Remove_permision().setVisible(true);
        });
    }
}
