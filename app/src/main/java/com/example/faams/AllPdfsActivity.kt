package com.example.faams

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.faams.databinding.ActivityAllPdfsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.SearchView
import android.view.Menu
import androidx.appcompat.app.AlertDialog

class AllPdfsActivity : AppCompatActivity(), PdfFilesAdapter.PdfClickListener {
    private lateinit var binding: ActivityAllPdfsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFilesAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private val pdfList = mutableListOf<PdfFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPdfsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        databaseReference =
            FirebaseDatabase.getInstance().reference.child("pdfs").child(currentUser!!.uid)
        initRecyclerView()
        getAllPdfs()
        initSearchView()
    }

    private fun getAllPdfs() {
        binding.progressBar.visibility = View.VISIBLE
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pdfFile = dataSnapshot.getValue(PdfFile::class.java)
                    pdfFile?.let {
                        it.key = dataSnapshot.key ?: ""
                        pdfList.add(it)
                    }
                }
                if (pdfList.isEmpty())
                    Toast.makeText(this@AllPdfsActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                adapter.submitList(pdfList.toList())
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllPdfsActivity, error.message, Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    private fun initRecyclerView() {
        binding.pdfsRecyclerView.setHasFixedSize(true)
        binding.pdfsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = PdfFilesAdapter(this)
        binding.pdfsRecyclerView.adapter = adapter
    }
    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.submitList(pdfList)
        } else {
            val filteredList = pdfList.filter {
                it.fileName.contains(query, ignoreCase = true)
            }
            adapter.submitList(filteredList)
        }
    }

    override fun onPdfClicked(pdfFile: PdfFile) {
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("fileName", pdfFile.fileName)
        intent.putExtra("downloadUrl", pdfFile.downloadUrl)
        startActivity(intent)
    }

    override fun onPdfLongClicked(pdfFile: PdfFile) {
        showDeleteConfirmationDialog(pdfFile)
    }

    private fun showDeleteConfirmationDialog(pdfFile: PdfFile) {
        AlertDialog.Builder(this)
            .setTitle("Delete PDF")
            .setMessage("Are you sure you want to delete ${pdfFile.fileName}?")
            .setPositiveButton("Yes") { dialog, _ ->
                deletePdfFile(pdfFile)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }



    private fun deletePdfFile(pdfFile: PdfFile) {
        val pdfRef = databaseReference.child(pdfFile.key)

        pdfRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "PDF deleted successfully", Toast.LENGTH_SHORT).show()
                pdfList.remove(pdfFile)
                adapter.submitList(pdfList.toList())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete PDF: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onDeletePdf(pdfFile: PdfFile) {
        deletePdfFile(pdfFile)
    }
}
