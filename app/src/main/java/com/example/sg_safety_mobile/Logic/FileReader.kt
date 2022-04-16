package com.example.sg_safety_mobile.Logic

/**
 * Class that act as reader of the file
 *
 * @since 2022-4-15
 */
class FileReader(val reader: Reader) {
    /**
     *Reader variables(val reader)
     */

    /**
     *Read the file uploaded
     */
    fun readFile(){
        reader.readFile()
    }
}