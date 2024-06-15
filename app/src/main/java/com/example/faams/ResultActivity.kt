

package com.example.faams

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faams.databinding.ActivityResultBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var pdfAdapter: PdfAdapter
    private val pdfList = mutableListOf<PdfItem>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfAdapter = PdfAdapter(pdfList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = pdfAdapter

        val query = intent.getStringExtra("query")
        if (query != null) {
            searchPdfs(query)
        }
    }

    private fun searchPdfs(query: String) {
        db.collection("pdfs")
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                pdfList.clear()
                for (document in documents) {
                    val pdf = document.toObject(PdfItem::class.java)
                    pdfList.add(pdf)
                }
                pdfAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


