import com.kotlin.socialstore.data.DataConstants

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

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

@RequiresApi(Build.VERSION_CODES.O)
fun localDateToDate(localDate: LocalDate): Date {
    val localDateTime = localDate.atStartOfDay()
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}