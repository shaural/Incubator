package cs408.incubator

class Idea {

    private lateinit var ideaTitle: String
    private lateinit var ideaDesc: String
    private lateinit var collaborators: String
    private lateinit var tags: String

    fun Idea(){
        ideaTitle = ""
        ideaDesc = ""
        collaborators = ""
        tags  = ""
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
        collaborators = collab
    }

    fun getCollaborators(): String {
        return collaborators
    }

    fun setTags(tags : String){
        this.tags = tags
    }

    fun getTags(): String {
        return tags
    }

}