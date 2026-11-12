package com.example.pulsofirme

import android.content.Context

/**
 * Ejercicios exclusivos de Premium Guiada.
 * OJO: antes de lanzar, que un kinesiólogo o terapista ocupacional revise
 * nombres, pasos, repeticiones y advertencias.
 */
data class EjercicioPremium(
    val id: String,
    val nombre: String,
    val zona: String,
    val tipo: String,
    val diagnostico: String,          // "Parkinson" o "Artritis"
    val descripcion: String,
    val pasos: List<String>,
    val repeticiones: Int,            // versión ON (normal)
    val segundos: Int,
    val repeticionesOff: Int,         // versión OFF (más suave)
    val segundosOff: Int,
    val notaOff: String,
    val advertencia: String,
    val premium: Boolean = true
)

/** Lo que realmente hay que hacer hoy: ya ajustado por nivel y por on/off. */
data class ParametrosEjercicio(
    val repeticiones: Int,
    val segundos: Int,
    val version: String,
    val nota: String?,
    val nivel: Int
)

object EjerciciosPremium {

    const val AVISO_APP =
        "PulsoFirme no reemplaza tu tratamiento. Consultá a tu profesional antes de empezar una rutina nueva."

    private const val ADV_ARTRITIS =
        "Si tenés un brote con dolor o inflamación, no hagas este ejercicio y consultá a tu profesional."
    private const val ADV_PARKINSON =
        "Hacelo sentado, con la espalda apoyada. Frená si te sentís mareado o con dolor."

    val todos: List<EjercicioPremium> = listOf(

        // ---------------- PARKINSON ----------------
        EjercicioPremium(
            id = "golpeteo_metronomo",
            nombre = "Golpeteo de dedos con metrónomo",
            zona = "Dedos", tipo = "Coordinación", diagnostico = "Parkinson",
            descripcion = "El pulgar toca cada dedo siguiendo un ritmo que va subiendo. El ritmo externo ayuda a que el movimiento salga más fluido.",
            pasos = listOf(
                "Apoyá el antebrazo en la mesa, con la mano relajada.",
                "Tocá con el pulgar cada dedo, del índice al meñique, al ritmo del sonido.",
                "Volvé del meñique al índice sin cortar el ritmo.",
                "Empezá lento y subí el ritmo solo si te sale parejo."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Ritmo más lento y pausas cortas entre series.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "movimientos_grandes",
            nombre = "Movimientos grandes",
            zona = "Mano", tipo = "Amplitud", diagnostico = "Parkinson",
            descripcion = "Abrir la mano exagerando el recorrido y cerrarla fuerte. Trabaja la amplitud del movimiento.",
            pasos = listOf(
                "Sentate derecho y llevá el brazo adelante.",
                "Abrí la mano estirando los dedos lo más que puedas.",
                "Cerrá el puño con fuerza y mantené 2 segundos.",
                "Abrí otra vez, exagerando el movimiento."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Movimientos amplios pero sin forzar. Descansá entre repeticiones.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "piano_mesa",
            nombre = "Piano en la mesa",
            zona = "Dedos", tipo = "Coordinación", diagnostico = "Parkinson",
            descripcion = "Levantar los dedos de a uno, en secuencias cada vez más largas.",
            pasos = listOf(
                "Apoyá la mano abierta sobre la mesa.",
                "Levantá un dedo por vez, del pulgar al meñique, y volvé a apoyarlo.",
                "Después probá secuencias salteadas (1-3-2-4, 5-2-4-1).",
                "Subí la velocidad solo si mantenés el control."
            ),
            repeticiones = 8, segundos = 90, repeticionesOff = 5, segundosOff = 60,
            notaOff = "Solo secuencias simples, de a un dedo y sin apuro.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "doble_tarea",
            nombre = "Doble tarea",
            zona = "Mano", tipo = "Coordinación", diagnostico = "Parkinson",
            descripcion = "Apretar una pelota blanda mientras contás hacia atrás, para entrenar hacer dos cosas a la vez.",
            pasos = listOf(
                "Tomá una pelota blanda con la mano.",
                "Apretala y soltala a un ritmo parejo.",
                "Al mismo tiempo, contá hacia atrás desde 20 de a uno.",
                "Si te perdés, frená y empezá de nuevo."
            ),
            repeticiones = 8, segundos = 120, repeticionesOff = 5, segundosOff = 90,
            notaOff = "Contá hacia atrás desde 10 y apretá más despacio.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "actividades_diarias",
            nombre = "Actividades diarias simuladas",
            zona = "Dedos", tipo = "Funcional", diagnostico = "Parkinson",
            descripcion = "Practicar gestos de todos los días: abotonar, girar una llave, tomar una moneda.",
            pasos = listOf(
                "Abotoná y desabotoná una prenda con botones.",
                "Girá una llave en una cerradura o en una tabla de práctica.",
                "Tomá una moneda de la mesa y dejala en un frasco.",
                "Repetí cada gesto con calma y sin apuro."
            ),
            repeticiones = 6, segundos = 150, repeticionesOff = 4, segundosOff = 120,
            notaOff = "Usá botones grandes y tomate todo el tiempo que necesites.",
            advertencia = ADV_PARKINSON
        ),

        // ------- PARKINSON (nuevos) -------
        EjercicioPremium(
            id = "escritura_grande",
            nombre = "Escritura grande",
            zona = "Mano", tipo = "Escritura", diagnostico = "Parkinson",
            descripcion = "Escribir con letra y trazos grandes para combatir la letra que se va achicando (micrografía).",
            pasos = listOf(
                "Sentate frente a una hoja grande y tomá un marcador grueso.",
                "Dibujá ondas y rulos grandes que crucen toda la hoja.",
                "Escribí tu nombre bien grande, pensando en cada letra.",
                "Si la letra se achica, frená, respirá y empezá la línea de nuevo."
            ),
            repeticiones = 6, segundos = 150, repeticionesOff = 4, segundosOff = 120,
            notaOff = "Solo ondas y rulos grandes, sin escribir palabras.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "pasar_objetos",
            nombre = "Pasar objetos entre manos",
            zona = "Manos", tipo = "Coordinación bimanual", diagnostico = "Parkinson",
            descripcion = "Pasar un objeto chico de una mano a la otra para que las dos manos trabajen juntas.",
            pasos = listOf(
                "Sentate con los brazos apoyados y una pelotita o un corcho en una mano.",
                "Pasala a la otra mano por delante del cuerpo.",
                "Cada vez, alejá un poco más las manos antes de pasarla.",
                "Mantené el ritmo parejo, sin apurarte."
            ),
            repeticiones = 12, segundos = 90, repeticionesOff = 8, segundosOff = 60,
            notaOff = "Manos cerca y un objeto más grande, fácil de agarrar.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "dar_vuelta_cartas",
            nombre = "Dar vuelta cartas",
            zona = "Dedos", tipo = "Destreza fina", diagnostico = "Parkinson",
            descripcion = "Levantar y dar vuelta naipes de a uno para entrenar la pinza entre el pulgar y el índice.",
            pasos = listOf(
                "Poné 10 cartas boca abajo sobre la mesa.",
                "Tomá cada carta con el pulgar y el índice.",
                "Dala vuelta y dejala boca arriba.",
                "Cuando termines, hacelo con la otra mano."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Usá 6 cartas y deslizalas hasta el borde de la mesa para agarrarlas.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "hombros_amplios",
            nombre = "Círculos amplios de hombros",
            zona = "Hombros", tipo = "Amplitud", diagnostico = "Parkinson",
            descripcion = "Movimientos grandes de hombros para soltar la rigidez del cuello y la espalda alta.",
            pasos = listOf(
                "Sentate derecho, con los brazos relajados a los costados.",
                "Subí los hombros hacia las orejas.",
                "Llevalos hacia atrás y bajalos, dibujando un círculo bien grande.",
                "Hacé la mitad de las vueltas hacia adelante."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Círculos más chicos y una pausa cada 3 vueltas.",
            advertencia = ADV_PARKINSON
        ),
        EjercicioPremium(
            id = "marcha_sentado",
            nombre = "Marcha sentado con ritmo",
            zona = "Piernas", tipo = "Ritmo", diagnostico = "Parkinson",
            descripcion = "Levantar las rodillas de forma alternada siguiendo un ritmo, como si caminaras sentado.",
            pasos = listOf(
                "Sentate en una silla firme, con los pies apoyados en el piso.",
                "Levantá la rodilla derecha y bajala.",
                "Levantá la izquierda, siguiendo el ritmo del sonido.",
                "Acompañá con los brazos, como cuando caminás."
            ),
            repeticiones = 20, segundos = 120, repeticionesOff = 12, segundosOff = 90,
            notaOff = "Ritmo lento y sin mover los brazos.",
            advertencia = ADV_PARKINSON
        ),

        // ---------------- ARTRITIS ----------------
        EjercicioPremium(
            id = "deslizamiento_tendones",
            nombre = "Deslizamiento de tendones",
            zona = "Mano", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Una serie de posiciones de la mano que mueve los tendones de forma suave.",
            pasos = listOf(
                "Empezá con la mano recta, dedos juntos.",
                "Doblá los dedos formando un gancho y mantené 3 segundos.",
                "Cerrá el puño completo y mantené 3 segundos.",
                "Hacé un puño con los dedos rectos y volvé a la mano recta."
            ),
            repeticiones = 8, segundos = 120, repeticionesOff = 5, segundosOff = 90,
            notaOff = "Movimientos más cortos, sin llegar al límite de comodidad.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "mesa_nudillos",
            nombre = "Mesa de nudillos",
            zona = "Mano", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Doblar solo los nudillos grandes, con los dedos rectos, como formando una mesa. Mantiene móviles las articulaciones de la base de los dedos.",
            pasos = listOf(
                "Apoyá el antebrazo en la mesa, con la mano de costado y los dedos estirados.",
                "Doblá solo los nudillos grandes, con los dedos rectos, formando una \"mesa\".",
                "Mantené la posición 3 segundos, sin apretar.",
                "Volvé a estirar los dedos despacio."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Doblá apenas y mantené solo 2 segundos.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "abduccion_pulgar",
            nombre = "Abducción y oposición del pulgar",
            zona = "Pulgar", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Movimientos suaves de apertura y de toque con el pulgar.",
            pasos = listOf(
                "Con la mano apoyada de costado, separá el pulgar de la palma lo más que sea cómodo.",
                "Volvé y tocá con la punta del pulgar la punta de cada dedo.",
                "Hacelo despacio, sin forzar."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Apertura corta y sin llegar al dedo meñique.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "extension_banda",
            nombre = "Extensión con banda elástica",
            zona = "Dedos", tipo = "Fuerza", diagnostico = "Artritis",
            descripcion = "Abrir la mano contra una resistencia suave.",
            pasos = listOf(
                "Poné una banda elástica suave alrededor de los dedos.",
                "Abrí la mano estirando los dedos contra la banda.",
                "Volvé despacio a la posición inicial."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Sin banda, o con una muy suave.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "muneca",
            nombre = "Movilidad de muñeca",
            zona = "Muñeca", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Flexión, extensión y desviación lateral de la muñeca.",
            pasos = listOf(
                "Apoyá el antebrazo en la mesa, con la mano fuera del borde.",
                "Subí y bajá la muñeca despacio (flexión y extensión).",
                "Movela hacia el lado del pulgar y hacia el del meñique."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Rango corto y sin forzar el final del movimiento.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "calentamiento_guiado",
            nombre = "Calentamiento guiado",
            zona = "Mano", tipo = "Calentamiento", diagnostico = "Artritis",
            descripcion = "Rutina de movilidad suave para después de la ducha tibia o antes de dormir.",
            pasos = listOf(
                "Abrí y cerrá las manos despacio, 10 veces.",
                "Girá las muñecas en círculos, hacia un lado y hacia el otro.",
                "Hacé círculos suaves con cada dedo.",
                "Terminá sacudiendo las manos con suavidad."
            ),
            repeticiones = 1, segundos = 300, repeticionesOff = 1, segundosOff = 180,
            notaOff = "Versión corta de 3 minutos, a ritmo muy tranquilo.",
            advertencia = ADV_ARTRITIS
        ),

        // ------- ARTRITIS (nuevos) -------
        EjercicioPremium(
            id = "abrir_frasco",
            nombre = "Abrir un frasco",
            zona = "Mano y muñeca", tipo = "Actividad diaria", diagnostico = "Artritis",
            descripcion = "Practicar abrir y cerrar la tapa de un frasco, uno de los gestos que más cuestan con artritis. Entrena la fuerza de agarre y el giro de la muñeca en una tarea real.",
            pasos = listOf(
                "Tomá un frasco con la tapa apenas enroscada y apoyalo en la mesa.",
                "Sostené el frasco firme con una mano y agarrá la tapa con la otra.",
                "Girá la tapa despacio para abrirla, usando la muñeca y no solo los dedos.",
                "Volvé a cerrarla y repetí. Después cambiá de mano."
            ),
            repeticiones = 8, segundos = 120, repeticionesOff = 5, segundosOff = 90,
            notaOff = "Usá un frasco de tapa ancha y dejala bien floja. Podés poner una gomita en la tapa para agarrarla mejor.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "amasar_plastilina",
            nombre = "Amasar plastilina",
            zona = "Mano y dedos", tipo = "Fuerza suave", diagnostico = "Artritis",
            descripcion = "Moldear una bola de plastilina o masa terapéutica: aplastar, enrollar y pellizcar. Calienta las articulaciones y trabaja toda la mano de forma suave y entretenida.",
            pasos = listOf(
                "Tomá una bola de plastilina blanda y hacela rodar entre las palmas.",
                "Apoyala en la mesa y aplastala despacio con la palma.",
                "Enrollala con los dedos estirados hasta formar un \"chorizo\".",
                "Pellizcá pedacitos con el pulgar y cada dedo, y volvé a armar la bola."
            ),
            repeticiones = 6, segundos = 180, repeticionesOff = 4, segundosOff = 120,
            notaOff = "Usá una masa bien blanda (o tibia) y hacé solo los pasos 1 y 2.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "porotos_frasco",
            nombre = "Porotos al frasco",
            zona = "Dedos", tipo = "Destreza fina", diagnostico = "Artritis",
            descripcion = "Tomar porotos de a uno con la punta de los dedos y pasarlos a un frasco. Trabaja la pinza fina y la precisión, sin hacer fuerza.",
            pasos = listOf(
                "Poné un puñado de porotos en un plato y un frasco abierto al lado.",
                "Tomá un poroto con la punta del pulgar y el índice.",
                "Llevalo hasta el frasco y soltalo adentro.",
                "Repetí cambiando de dedo: pulgar con mayor, anular y meñique."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Usá objetos más grandes, como botones o fichas, y andá sin apuro.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "separar_dedos",
            nombre = "Separar los dedos",
            zona = "Dedos", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Abrir los dedos en abanico sobre la mesa para mantener el movimiento entre ellos.",
            pasos = listOf(
                "Apoyá la palma en la mesa, con los dedos juntos.",
                "Separá los dedos todo lo que sea cómodo, como un abanico.",
                "Mantené 3 segundos.",
                "Volvé a juntarlos despacio."
            ),
            repeticiones = 10, segundos = 90, repeticionesOff = 6, segundosOff = 60,
            notaOff = "Separación corta y solo 2 segundos.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "arrugar_toalla",
            nombre = "Arrugar una toalla",
            zona = "Dedos", tipo = "Fuerza suave", diagnostico = "Artritis",
            descripcion = "Juntar una toalla hacia la palma usando solo las puntas de los dedos.",
            pasos = listOf(
                "Extendé una toalla chica sobre la mesa.",
                "Apoyá la mano encima, con la palma plana.",
                "Con las puntas de los dedos, arrastrá la toalla hacia la palma.",
                "Estirala otra vez y repetí."
            ),
            repeticiones = 8, segundos = 120, repeticionesOff = 5, segundosOff = 90,
            notaOff = "Usá una tela más liviana, como un pañuelo.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "estiramiento_munieca_mesa",
            nombre = "Estiramiento de muñeca en la mesa",
            zona = "Muñeca", tipo = "Estiramiento", diagnostico = "Artritis",
            descripcion = "Estirar la parte de adentro de la muñeca y la palma apoyando la mano en la mesa.",
            pasos = listOf(
                "Parate frente a una mesa y apoyá las palmas con los dedos hacia vos.",
                "Inclinate apenas hacia atrás hasta sentir un estiramiento suave.",
                "Mantené 15 segundos, respirando tranquilo.",
                "Aflojá y descansá antes de repetir."
            ),
            repeticiones = 3, segundos = 90, repeticionesOff = 2, segundosOff = 60,
            notaOff = "Hacelo sentado, con una sola mano y menos inclinación.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "pendulo_hombro",
            nombre = "Péndulo de hombro",
            zona = "Hombro", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Dejar colgar el brazo y moverlo como un péndulo para aflojar el hombro sin hacer fuerza.",
            pasos = listOf(
                "Apoyá una mano en una mesa firme e inclinate un poco hacia adelante.",
                "Dejá colgar el otro brazo, bien relajado.",
                "Movelo en círculos chicos usando el balanceo del cuerpo.",
                "Cambiá de sentido y después de brazo."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Solo balanceo adelante y atrás, sin círculos.",
            advertencia = ADV_ARTRITIS
        ),
        EjercicioPremium(
            id = "circulos_tobillo",
            nombre = "Círculos de tobillo",
            zona = "Tobillos y pies", tipo = "Movilidad", diagnostico = "Artritis",
            descripcion = "Mover los tobillos en círculos y los dedos del pie para reducir la rigidez de los pies.",
            pasos = listOf(
                "Sentate y estirá una pierna, con el talón apoyado en el piso.",
                "Dibujá círculos con el pie hacia un lado y hacia el otro.",
                "Abrí y cerrá los dedos del pie.",
                "Cambiá de pierna."
            ),
            repeticiones = 10, segundos = 120, repeticionesOff = 6, segundosOff = 90,
            notaOff = "Círculos chicos, de a un sentido por vez.",
            advertencia = ADV_ARTRITIS
        )
    )

    /** Regla de bloqueo: un ejercicio premium solo se puede usar con Premium activo. */
    fun bloqueado(ejercicio: EjercicioPremium): Boolean =
        ejercicio.premium && !UserData.esPremium

    // Los que se muestran en la app por diagnóstico (en este orden):
    // 6 de Parkinson y 5 de artritis.
    // Para cambiar uno, reemplazá su id por el de otro ejercicio de "todos".
    private val seleccionParkinson = listOf(
        "escritura_grande", "pasar_objetos", "dar_vuelta_cartas",
        "hombros_amplios", "marcha_sentado", "doble_tarea"
    )
    private val seleccionArtritis = listOf(
        "separar_dedos", "arrugar_toalla", "estiramiento_munieca_mesa",
        "abrir_frasco", "circulos_tobillo"
    )

    /** Los ejercicios Premium que se muestran de un diagnóstico ("Parkinson" o "Artritis"). */
    fun seleccion(diagnostico: String): List<EjercicioPremium> {
        val ids = if (diagnostico.equals("Artritis", ignoreCase = true)) seleccionArtritis else seleccionParkinson
        return ids.mapNotNull { porId(it) }
    }

    fun porId(id: String): EjercicioPremium? = todos.firstOrNull { it.id == id }

    fun porNombre(nombre: String): EjercicioPremium? =
        todos.firstOrNull { it.nombre.equals(nombre, ignoreCase = true) }

    /** Repeticiones y duración de hoy: versión on/off + nivel de dificultad adaptativa. */
    fun parametros(context: Context, ejercicio: EjercicioPremium): ParametrosEjercicio {
        val r = DificultadAdaptativa.vigente(context)
        val off = r.enOff
        val repsBase = if (off) ejercicio.repeticionesOff else ejercicio.repeticiones
        val segBase = if (off) ejercicio.segundosOff else ejercicio.segundos
        return ParametrosEjercicio(
            repeticiones = DificultadAdaptativa.escalar(repsBase, r),
            segundos = DificultadAdaptativa.escalar(segBase, r),
            version = r.variante,
            nota = if (off) ejercicio.notaOff else null,
            nivel = r.nivel
        )
    }
}