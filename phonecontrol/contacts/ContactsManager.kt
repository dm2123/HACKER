package com.example.hacker.phonecontrol.contacts

import android.content.ContentResolver
import android.net.Uri

/** Contacts manager */
class ContactsManager(private val contentResolver: ContentResolver) {
    /** Get all contacts */
    fun getAllContacts(): List<Contact> {
        // TODO: Implement query
        return emptyList()
    }

    /** Get contact by ID */
    fun getContact(id: String): Contact? {
        // TODO: Implement query
        return null
    }

    /** Dial a contact */
    fun dialContact(contact: Contact) {
        // TODO: Implement intent
    }
}

/** Contact model */
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String?
)