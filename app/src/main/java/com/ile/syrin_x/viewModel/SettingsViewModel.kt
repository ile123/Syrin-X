package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.DataStoreRepository
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.GetUserPremiumStatusUseCase
import com.ile.syrin_x.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getUserPremiumStatusUseCase: GetUserPremiumStatusUseCase,
    private val dataStoreRepo: DataStoreRepository
) : ViewModel() {

    companion object {
        private const val THEME_KEY = "app_theme"
    }

    private val _settingsFlow = MutableSharedFlow<Response<Any>>()
    val settingsFlow: SharedFlow<Response<Any>> = _settingsFlow.asSharedFlow()

    private val _isUserPremium = MutableStateFlow(false)
    val isUserPremium: StateFlow<Boolean> = _isUserPremium.asStateFlow()

    val currentTheme: StateFlow<AppTheme> =
        dataStoreRepo
            .getStringFlow(THEME_KEY)
            .map { name ->
                AppTheme.entries.find { it.name == name }
                    ?: AppTheme.SystemDefault
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                AppTheme.SystemDefault
            )

    val themeList: List<String> = AppTheme.entries
        .filter { it != AppTheme.Light && it != AppTheme.Dark }
        .map { it.displayName }

    fun changeAppTheme(themeDisplayName: String) {
        val selected = AppTheme.entries
            .firstOrNull { it.displayName == themeDisplayName }
            ?: return

        viewModelScope.launch {
            dataStoreRepo.putString(THEME_KEY, selected.name)
            _settingsFlow.emit(Response.Success(selected))
        }
    }

    fun checkIfUserIsPremium() = viewModelScope.launch {
        getUserUidUseCase().collect { userUuid ->
            getUserPremiumStatusUseCase(userUuid).collect { response ->
                when (response) {
                    is Response.Error   -> Log.d("SettingsError", "Could not check premium.")
                    is Response.Loading -> { }
                    is Response.Success<*> ->
                        _isUserPremium.value = response.data as Boolean
                }
            }
        }
    }
}