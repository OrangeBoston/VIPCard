package com.orangeboston.vipcard

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditCardDialogFragment : DialogFragment() {

    var onConfirm: ((storeName: String, phoneNumber: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_card, null)
        val editStoreName = view.findViewById<EditText>(R.id.editStoreName)
        val editPhoneNumber = view.findViewById<EditText>(R.id.editPhoneNumber)

        arguments?.let {
            editStoreName.setText(it.getString(ARG_STORE_NAME))
            editPhoneNumber.setText(it.getString(ARG_PHONE_NUMBER))
        }

        val isEdit = (arguments?.getLong(ARG_CARD_ID, -1L) ?: -1L) > 0

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (isEdit) R.string.edit_card else R.string.add_card)
            .setView(view)
            .setPositiveButton(R.string.btn_save) { _, _ ->
                val store = editStoreName.text.toString().trim()
                val phone = editPhoneNumber.text.toString().trim()
                if (store.isNotEmpty() && phone.isNotEmpty()) {
                    onConfirm?.invoke(store, phone)
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }

    companion object {
        const val TAG = "EditCardDialog"
        private const val ARG_CARD_ID = "cardId"
        private const val ARG_STORE_NAME = "storeName"
        private const val ARG_PHONE_NUMBER = "phoneNumber"

        fun newInstance(card: MemberCard? = null): EditCardDialogFragment {
            return EditCardDialogFragment().apply {
                arguments = Bundle().apply {
                    if (card != null) {
                        putLong(ARG_CARD_ID, card.id)
                        putString(ARG_STORE_NAME, card.storeName)
                        putString(ARG_PHONE_NUMBER, card.phoneNumber)
                    }
                }
            }
        }
    }
}
