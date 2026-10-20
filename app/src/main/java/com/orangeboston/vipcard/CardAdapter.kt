package com.orangeboston.vipcard

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val onClick: (MemberCard) -> Unit,
    private val onSetDefault: (MemberCard) -> Unit,
    private val onEdit: (MemberCard) -> Unit,
    private val onDelete: (MemberCard) -> Unit
) : ListAdapter<MemberCard, CardAdapter.VH>(DIFF) {

    private var defaultCardId: Long = CardRepository.NO_DEFAULT

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val storeName: TextView = view.findViewById(R.id.textStoreName)
        val phoneNumber: TextView = view.findViewById(R.id.textPhoneNumber)
        val defaultBadge: TextView = view.findViewById(R.id.textDefaultBadge)
        val btnDefault: ImageView = view.findViewById(R.id.btnDefault)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    fun submitCards(cards: List<MemberCard>, defaultId: Long) {
        defaultCardId = defaultId
        submitList(cards)
    }

    fun setDefaultCardId(id: Long) {
        if (defaultCardId == id) return
        defaultCardId = id
        if (itemCount > 0) notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val card = getItem(position)
        val isDefault = card.id == defaultCardId

        holder.storeName.text = card.storeName
        holder.phoneNumber.text = card.phoneNumber
        holder.defaultBadge.visibility = if (isDefault) View.VISIBLE else View.GONE
        holder.btnDefault.setImageResource(
            if (isDefault) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
        holder.btnDefault.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isDefault) R.color.accent else R.color.text_secondary
            )
        )

        holder.itemView.setOnClickListener { onClick(card) }
        holder.btnDefault.setOnClickListener { onSetDefault(card) }
        holder.btnEdit.setOnClickListener { onEdit(card) }
        holder.btnDelete.setOnClickListener { onDelete(card) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MemberCard>() {
            override fun areItemsTheSame(a: MemberCard, b: MemberCard) = a.id == b.id
            override fun areContentsTheSame(a: MemberCard, b: MemberCard) = a == b
        }
    }
}
