package com.ile.syrin_x.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.database.NewReleaseNotificationDao
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.GetUsersNotificationsUseCase
import com.ile.syrin_x.domain.usecase.user.MarkUserNotificationAsSeenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewReleaseNotificationViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getAllNotificationsUseCase: GetUsersNotificationsUseCase,
    private val markUserNotificationAsSeenUseCase: MarkUserNotificationAsSeenUseCase
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NewReleaseNotificationEntity>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _dataFlow = MutableSharedFlow<Response<Any>>()
    val dataFlow = _dataFlow

    fun getAllNotifications() = viewModelScope.launch {
        val userId = getUserUidUseCase().firstOrNull() ?: return@launch
        getAllNotificationsUseCase(userId).collect { response ->
            when (response) {
                is Response.Success -> {
                    @Suppress("UNCHECKED_CAST")
                    _notifications.value = response.data
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> { }
            }
        }
    }

    fun markAsSeen(trackId: Long) = viewModelScope.launch {
        val userId = getUserUidUseCase().firstOrNull() ?: return@launch
        markUserNotificationAsSeenUseCase(userId, trackId)
    }
}