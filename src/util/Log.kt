package util

object Log {
	private val log = ArrayList<Message>()
	const val VERBOSE = 0
	const val INFO = 1
	const val ERROR = 2
	const val FATAL = 3

	@JvmStatic
	fun verbose(message: String) {
		log.add(Message(message, VERBOSE))
	}
	
	@JvmStatic
	fun info(message: String) {
		log.add(Message(message, INFO))
	}
	
	@JvmStatic
	fun error(message: String) {
		log.add(Message(message, ERROR))
	}
	
	@JvmStatic
	fun fatal(message: String) {
		System.err.println(message)
		log.add(Message(message, FATAL))
	}
	
	@JvmStatic
	fun print() {
		log.forEach{m -> if(m.priority > INFO) System.err.println(m.message) else System.out.println(m.message)}
	}
	
	@JvmStatic
	fun print(priority: Int) {
		log.filter{m -> m.priority >= priority}.forEach{m -> System.out.println(m.message)}
	}
}

class Message(message: String, priority: Int) {
	val message = message
	val priority = priority
}