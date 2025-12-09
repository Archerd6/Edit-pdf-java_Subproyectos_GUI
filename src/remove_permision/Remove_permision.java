package remove_permision;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

public class Remove_permision
{
    public static void main(String[] args)
    {
        try {
            ejecutar();
        } catch (IOException e) {
            System.err.println("Error eliminando permisos: " + e.getMessage());
        }
    }

    static void ejecutar() throws IOException
    {
        String INPUT_PATH = System.getProperty("user.dir") + "\\pdf_In";
        String OUTPUT_PATH = System.getProperty("user.dir") + "\\pdf_Out";

        new File(INPUT_PATH).mkdirs();
        new File(OUTPUT_PATH).mkdirs();

        File[] files = new File(INPUT_PATH).listFiles();

        if (files == null)
        {
            System.out.println("No hay archivos en " + INPUT_PATH);
            return;
        }

        for (File f : files)
        {
            if (!f.getName().toLowerCase().endsWith(".pdf")) continue;

            System.out.println("Procesando: " + f.getName());

            PDDocument document = null;

            try
            {
                // Cargar PDF (si tiene contraseña de usuario, necesita una)
                document = Loader.loadPDF(f);

                // ESTA ES LA LÍNEA IMPORTANTE
                document.setAllSecurityToBeRemoved(true);

                // Guardar PDF sin restricciones
                document.save(OUTPUT_PATH + "\\" + f.getName());

                System.out.println(" → Permisos eliminados. Guardado en: pdf_Out\\" + f.getName());
            }
            catch (Exception e)
            {
                System.err.println("Error procesando " + f.getName() + ": " + e.getMessage());
            }
            finally
            {
                if (document != null) document.close();
            }
        }
    }
}
