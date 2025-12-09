package remove_metadata;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class RemoveMetadata
{
    public static void main(String[] args)
    {
        try {
            Ejecutar();
        } catch (IOException e) {
            System.err.println("Error eliminando metadatos: " + e.getMessage());
        }
    }

    static void Ejecutar() throws IOException
    {
        String INPUT_PATH = System.getProperty("user.dir") + "\\pdf_In";
        String OUTPUT_PATH = System.getProperty("user.dir") + "\\pdf_Out";

        // Crear carpetas si no existen
        File dirIn = new File(INPUT_PATH);
        dirIn.mkdirs();
        File dirOut = new File(OUTPUT_PATH);
        dirOut.mkdirs();

        File[] directoryListing = dirIn.listFiles();

        if (directoryListing == null)
        {
            System.out.println("No hay archivos en " + INPUT_PATH);
            return;
        }

        for (File child : directoryListing)
        {
            String name = child.getName();

            if (name.endsWith(".pdf"))
            {
                File pdf = new File(INPUT_PATH + "\\" + name);

                System.out.println("Procesando: " + name);

                PDDocument document = null;

                try
                {
                    document = Loader.loadPDF(pdf);

                    // Quitar restricciones de seguridad
                    document.setAllSecurityToBeRemoved(true);

                    // Borrar DocumentInformation
                    PDDocumentInformation info = document.getDocumentInformation();
                    if (info != null)
                    {
                        document.setDocumentInformation(new PDDocumentInformation());
                        System.out.println(" - Metadatos básicos eliminados.");
                    }

                    // Borrar XMP Metadata del catalog
                    document.getDocumentCatalog().setMetadata(null);
                    System.out.println(" - Metadatos XMP eliminados.");

                    // Guardar archivo limpio
                    document.save(OUTPUT_PATH + "\\" + name);
                    System.out.println(" → Guardado limpio en: pdf_Out\\" + name);
                }
                catch (Exception e)
                {
                    System.err.println("Error procesando " + name + ": " + e.getMessage());
                }
                finally
                {
                    if (document != null)
                        document.close();
                }
            }
        }
    }
}
