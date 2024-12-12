package com.example.expense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.example.expense.Tools.Constraints;

public class PDFViewer extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        // Initialize the PDFView
        pdfView = findViewById(R.id.pdfView);

        // Load the PDF file using Barteksc library
        if (Constraints.path != null) {
            pdfView.fromFile(new java.io.File(Constraints.path))
                    .enableSwipe(true) // Allows swiping to change pages
                    .swipeHorizontal(false) // Sets swipe to vertical
                    .enableDoubletap(true) // Enables double-tap to zoom
                    .defaultPage(0) // Opens the PDF at the first page
                    .load(); // Load the PDF
        } else {
            // Handle the case where the path is null
            // You can show an error message or use a default file
        }
    }
}