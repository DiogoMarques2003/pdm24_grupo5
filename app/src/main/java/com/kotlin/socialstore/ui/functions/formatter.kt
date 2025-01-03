import com.kotlin.socialstore.data.DataConstants

fun formatWeekDay(weekDay: Long): String {
    return when (weekDay) {
        1L -> "Sunday"
        2L -> "Monday"
        3L -> "Tuesday"
        4L -> "Wednesday"
        5L -> "Thursday"
        6L -> "Friday"
        7L -> "Saturday"
        else -> "Invalid Weekday"
    }
}

fun formatAccountType(accType: String): String {
    return when(accType) {
        DataConstants.AccountType.volunteer -> "Volunteer"
        DataConstants.AccountType.benefiaryy -> "Beneficiary"
        else -> "Unknown"
    }
}