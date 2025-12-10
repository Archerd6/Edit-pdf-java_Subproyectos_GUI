package remove_permision;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class Remove_permision extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea log;
    private File[] selectedFiles;

    public Remove_permision() {
        setTitle("Quitar Permisos de PDF – PDFBox");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JButton btnSelect = new JButton("Seleccionar PDFs");
        JButton btnProcess = new JButton("Quitar Permisos");

        btnSelect.setFocusable(false);
        btnProcess.setFocusable(false);

        btnSelect.addActionListener(this::selectFiles);
        btnProcess.addActionListener(this::processFiles);

        panel.add(btnSelect);
        panel.add(btnProcess);

        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(580, 350));

        add(panel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void selectFiles(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivos PDF");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFiles = fileChooser.getSelectedFiles();

            log.append("Archivos seleccionados:\n");
            for (File f : selectedFiles) {
                log.append(" - " + f.getName() + "\n");
            }
            log.append("\n");
        }
    }

    private void processFiles(ActionEvent e) {
        if (selectedFiles == null || selectedFiles.length == 0) {
            log.append("❌ No has seleccionado ningún PDF.\n\n");
            return;
        }

        File outDir = new File("pdf_Out");
        outDir.mkdirs();

        for (File f : selectedFiles) {
            if (!f.getName().toLowerCase().endsWith(".pdf")) continue;

            log.append("Procesando: " + f.getName() + "\n");

            try (PDDocument document = Loader.loadPDF(f)) {

                // 🔥 Eliminar permisos / restricciones
                document.setAllSecurityToBeRemoved(true);

                File output = new File(outDir, f.getName());
                document.save(output);

                log.append(" ✔ Permisos eliminados → Guardado en: pdf_Out/" + f.getName() + "\n\n");
            }
            catch (Exception ex) {
                log.append(" ❌ Error procesando " + f.getName() + ": " + ex.getMessage() + "\n\n");
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new Remove_permision().setVisible(true);
        });
    }
}
