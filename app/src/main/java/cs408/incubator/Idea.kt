package cs408.incubator

import java.text.SimpleDateFormat
import java.util.*

class Idea {

    private lateinit var ideaTitle: String
    private lateinit var ideaDesc: String
    private lateinit var collaborators: ArrayList<String>
    private lateinit var tags: ArrayList<String>

    fun Idea(){
        ideaTitle = ""
        ideaDesc = ""
        collaborators = ArrayList()
        tags  = ArrayList()
    }

    fun setTitle(title: String){
        ideaTitle = title
    }

    fun getTitle() : String {
        return ideaTitle
    }

    fun setDesc(desc: String){
        ideaDesc = desc
    }

    fun getDesc() : String {
        return ideaDesc
    }

    fun setCollaborators(collab : String){
        collaborators = ArrayList()

        if(collab.isNotEmpty()){
            if(collab.contains(",")){
                val users = collab.split(",")
                for(u in users){
                    val user = u.trim()
                    if(!collaborators.contains(user))
                        collaborators.add(user)
                }
            }
            else
                collaborators.add(collab)
        }

    }

    fun getCollaborators(): ArrayList<String> {
        return collaborators
    }

    fun setTags(tag : String){

        tags = ArrayList()

        if(tag.isNotEmpty()){
            if(tag.contains(",")){
                val tagList = tag.split(",")
                for(u in tagList){
                    tags.add(u.trim())
                }
            }
            else
                tags.add(tag)
        }

    }

    fun getTags(): ArrayList<String> {
        return tags
    }

    //create string for log
    fun genLogStr(idea: String, user: String, action: String,obj: String, data: String): String {
        var date_format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        var time = Calendar.getInstance().time
        var str_log = date_format.format(time).toString() + "-" + user + "-" + action + "-" + obj + "-" + data
        return str_log
    }

}