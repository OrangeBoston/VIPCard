package com.orangeboston.vipcard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object CardRepository {

    private const val FILE_NAME = "cards.json"

    private fun file(context: Context): File =
        File(context.filesDir, FILE_NAME)

    fun loadCards(context: Context): MutableList<MemberCard> {
        val f = file(context)
        if (!f.exists()) return mutableListOf()
        val text = f.readText().ifBlank { return mutableListOf() }
        val arr = JSONArray(text)
        val list = mutableListOf<MemberCard>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                MemberCard(
                    id = obj.getLong("id"),
                    storeName = obj.getString("storeName"),
                    phoneNumber = obj.getString("phoneNumber")
                )
            )
        }
        return list
    }

    fun saveCards(context: Context, cards: List<MemberCard>) {
        val arr = JSONArray()
        for (card in cards) {
            val obj = JSONObject().apply {
                put("id", card.id)
                put("storeName", card.storeName)
                put("phoneNumber", card.phoneNumber)
            }
            arr.put(obj)
        }
        file(context).writeText(arr.toString())
    }

    fun addCard(context: Context, card: MemberCard): MutableList<MemberCard> {
        val cards = loadCards(context)
        cards.add(card)
        saveCards(context, cards)
        return cards
    }

    fun updateCard(context: Context, card: MemberCard): MutableList<MemberCard> {
        val cards = loadCards(context)
        val index = cards.indexOfFirst { it.id == card.id }
        if (index >= 0) {
            cards[index] = cards[index].copy(storeName = card.storeName, phoneNumber = card.phoneNumber)
        }
        saveCards(context, cards)
        return cards
    }

    fun deleteCard(context: Context, cardId: Long): MutableList<MemberCard> {
        val cards = loadCards(context)
        cards.removeAll { it.id == cardId }
        saveCards(context, cards)
        if (getDefaultCardId(context) == cardId) {
            setDefaultCardId(context, NO_DEFAULT)
        }
        return cards
    }

    fun getDefaultCardId(context: Context): Long =
        prefs(context).getLong(KEY_DEFAULT_CARD_ID, NO_DEFAULT)

    fun setDefaultCardId(context: Context, cardId: Long) {
        prefs(context).edit().putLong(KEY_DEFAULT_CARD_ID, cardId).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    const val NO_DEFAULT = -1L
    private const val KEY_DEFAULT_CARD_ID = "default_card_id"
    private const val PREFS_NAME = "vipcard_prefs"
}
