package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.UserEntity
import com.example.data.repository.StudyProRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(private val repository: StudyProRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Ensure Primary Admin (gangalamit005@gmail.com) is provisioned
                repository.ensurePrimaryAdminConfigured()

                // Default auto-login to Primary Admin for smooth testing and immediate verification
                val adminResult = repository.loginUser("gangalamit005@gmail.com", "admin")
                if (adminResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(currentUser = adminResult.getOrNull())
                } else {
                    val student = repository.loginUser("student@studypro.in", "user")
                    student.onSuccess { user ->
                        _uiState.value = _uiState.value.copy(currentUser = user)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "કૃપા કરીને ઈમેઈલ અને પાસવર્ડ બંને દાખલ કરો.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.loginUser(email, pass)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoading = false,
                    successMessage = "સ્વાગત છે, ${user.name}!"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "લોગિન નિષ્ફળ ગયું."
                )
            }
        }
    }

    fun register(name: String, email: String, pass: String, exam: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "કૃપા કરીને બધી વિગતો ભરો.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.registerUser(name, email, pass, exam)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoading = false,
                    successMessage = "નોંધણી સફળ થઈ! સ્વાગત છે."
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "નોંધણી નિષ્ફળ ગઈ."
                )
            }
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(currentUser = null, successMessage = "સફળતાપૂર્વક લોગ આઉટ થયા.")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun toggleAdminPreviewMode() {
        val user = _uiState.value.currentUser ?: return
        if (com.example.data.repository.AuthConfig.isPrimaryAdmin(user.email)) {
            // Primary admin can toggle preview mode between ADMIN and USER for testing
            val newRole = if (user.role == "ADMIN") "USER" else "ADMIN"
            viewModelScope.launch {
                val updated = user.copy(role = newRole)
                repository.updateUser(updated)
                _uiState.value = _uiState.value.copy(
                    currentUser = updated,
                    successMessage = if (newRole == "ADMIN") "એડમિન મોડ સક્રિય થયો." else "વિદ્યાર્થી પ્રિવ્યૂ મોડ સક્રિય થયો."
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "ફક્ત માન્ય પ્રાયમરી એડમિનિસ્ટ્રેટર જ આ મોડ બદલી શકે છે.")
        }
    }

    fun updateTargetExam(examName: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val updated = user.copy(targetExam = examName)
            repository.updateUser(updated)
            _uiState.value = _uiState.value.copy(currentUser = updated)
        }
    }
}
