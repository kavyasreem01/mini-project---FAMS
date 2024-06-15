package com.example.faams

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faams.databinding.ActivitySearchBinding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import android.content.Intent
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var pdfAdapter: PdfAdapter
    private val pdfList = mutableListOf<PdfItem>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfAdapter = PdfAdapter(pdfList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = pdfAdapter
        binding.searchView.requestFocus()
        binding.searchView.onActionViewExpanded()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchPdfs(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    searchPdfs(newText)
                }else {
                    pdfList.clear()
                    pdfAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }
    /*private fun navigateToResultActivity(query: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("query", query)
        startActivity(intent)
    }
}*/
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


