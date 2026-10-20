package com.orangeboston.vipcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CardAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var textEmpty: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recycler = findViewById(R.id.recyclerCards)
        textEmpty = findViewById(R.id.textEmpty)

        adapter = CardAdapter(
            onClick = { card ->
                startActivity(DisplayCardActivity.newIntent(this, card))
            },
            onSetDefault = { card ->
                val currentDefault = CardRepository.getDefaultCardId(this)
                val newDefault = if (currentDefault == card.id) {
                    CardRepository.NO_DEFAULT
                } else {
                    card.id
                }
                CardRepository.setDefaultCardId(this, newDefault)
                adapter.setDefaultCardId(newDefault)
            },
            onEdit = { card ->
                showEditDialog(card)
            },
            onDelete = { card ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(getString(R.string.delete_confirm_msg, card.storeName))
                    .setPositiveButton(R.string.btn_delete) { _, _ ->
                        CardRepository.deleteCard(this, card.id)
                        refreshList()
                        Snackbar.make(recycler, R.string.deleted, Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show()
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showEditDialog(null)
        }

        refreshList()

        if (savedInstanceState == null) {
            openDefaultCard()
        }
    }

    private fun openDefaultCard() {
        val defaultId = CardRepository.getDefaultCardId(this)
        if (defaultId == CardRepository.NO_DEFAULT) return
        val card = CardRepository.loadCards(this).firstOrNull { it.id == defaultId }
        if (card == null) {
            CardRepository.setDefaultCardId(this, CardRepository.NO_DEFAULT)
            return
        }
        startActivity(DisplayCardActivity.newIntent(this, card))
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val cards = CardRepository.loadCards(this)
        adapter.submitCards(cards, CardRepository.getDefaultCardId(this))
        textEmpty.visibility = if (cards.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showEditDialog(card: MemberCard?) {
        val dialog = EditCardDialogFragment.newInstance(card)
        dialog.onConfirm = { storeName, phoneNumber ->
            if (card == null) {
                CardRepository.addCard(
                    this,
                    MemberCard(storeName = storeName, phoneNumber = phoneNumber)
                )
            } else {
                CardRepository.updateCard(
                    this,
                    card.copy(storeName = storeName, phoneNumber = phoneNumber)
                )
            }
            refreshList()
        }
        dialog.show(supportFragmentManager, EditCardDialogFragment.TAG)
    }
}
