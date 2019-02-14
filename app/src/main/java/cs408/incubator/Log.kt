package cs408.incubator

import java.text.SimpleDateFormat
import java.util.*

//create string for log
fun genLogStr(user: String, action: String,obj: String, data: String): String {
    var date_format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    var time = Calendar.getInstance().time
    var str_log = date_format.format(time).toString() + "-" + user + "-" + action + "-" + obj + "-" + data
    return str_log
}