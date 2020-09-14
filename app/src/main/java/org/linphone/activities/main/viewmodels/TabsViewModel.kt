/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.core.*

class TabsViewModel : ViewModel() {
    val showHistory: Boolean = corePreferences.showHistory
    val showContacts: Boolean = corePreferences.showContacts
    val showDialer: Boolean = corePreferences.showDialer
    val showChat: Boolean = corePreferences.showChat

    val historySelected = MutableLiveData<Boolean>()
    val contactsSelected = MutableLiveData<Boolean>()
    val dialerSelected = MutableLiveData<Boolean>()
    val chatSelected = MutableLiveData<Boolean>()

    val unreadMessagesCount = MutableLiveData<Int>()
    val missedCallsCount = MutableLiveData<Int>()

    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            if (state == Call.State.End || state == Call.State.Error) {
                updateMissedCallCount()
            }
        }

        override fun onChatRoomRead(core: Core, chatRoom: ChatRoom) {
            updateUnreadChatCount()
        }

        override fun onMessageReceived(core: Core, chatRoom: ChatRoom, message: ChatMessage) {
            updateUnreadChatCount()
        }

        override fun onChatRoomStateChanged(core: Core, chatRoom: ChatRoom, state: ChatRoom.State) {
            if (state == ChatRoom.State.Deleted) {
                updateUnreadChatCount()
            }
        }
    }

    init {
        coreContext.core.addListener(listener)

        updateUnreadChatCount()
        updateMissedCallCount()
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)
        super.onCleared()
    }

    fun updateMissedCallCount() {
        missedCallsCount.value = coreContext.core.missedCallsCount
    }

    fun updateUnreadChatCount() {
        unreadMessagesCount.value = coreContext.core.unreadChatMessageCountFromActiveLocals
    }
}