package test

class JavaTest2 {
    fun findLongest(values: List<String>?): String? {
        if (values!!.isEmpty()) return null

        var result: String? = null
        for (value in values!!) {
            if (value.length > result!!.length) {
                result = value
            }
        }

        return result
    }
}
