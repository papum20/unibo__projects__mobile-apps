
# Specifications (italian)
​
Vorrei creare un mezzo per gestire l'inventario della casa (oggetti consumabili) e la cucina, con un focus sull'alimentazione: l'app dovrebbe accompagnare l'utente nel suo percorso dal negozio alla tavola, permettendogli di pianificare la spesa, tenere traccia delle scorte che possiede (e di cosa sta per finire) e programmare i pasti basandosi su di esse, con la possibilità di contare l'apporto di calorie e macronutrienti.

## features
1.	lista della spesa;
2.	ricette: si può creare e salvare una ricetta;
3.	pianificazione dei pasti: si possono assegnare i cibi/ricette che si consumano a una giornata. Vengono mostrati calorie e macronutrienti per ogni alimento/giorno/settimana...;
4.	inventario: lista di oggetti posseduti, di cui si salva la quantità. Gli oggetti possono essere aggiunti (o rimossi) automaticamente aggiungendo una spesa effettuata (per es. copiando lo scontrino, o scansionandolo con la telecamera e convertendolo in testo*), o manualmente, uno per uno (per es. per effettuare correzioni), mentre vengono rimossi quando si aggiungono a un pasto;
5.	esportazione/importazione dati: si possono esportare dati (per es. ricette) come archivio - per esempio, come backup o per condividerli;
6.	condivisione dati: per es. si condivide il suddetto archivio con qualcuno (che lo potrà poi importare nell'app dal file);
7.	notifiche: si possono impostare notifiche per quando le scorte di un determinato oggetto scendono sotto una certa soglia (per es. se rimangono meno di 0,5kg di farina).
8.	ricerca prezzo migliore per un prodotto, in base alle esperienze degli altri utenti: ogni utente, alla registrazione di un acquisto, invia a un'API esterna il prezzo pagato per il prodotto, e può visualizzare il negozio (tra quelli in un certo raggio dalla sua posizione) che offre al miglior prezzo un dato prodotto (soprattutto per quelli nella sua lista della spesa);
9.	suggerimento dei prodotti più convenienti in un negozio: l'utente, quando si trova in un negozio, può visualizzare quali prodotti, tra quelli nella lista della spesa, gli convenga comprare lì, e quali meglio se da un'altra parte.

*Nota: essendo gli scontrini scritti in modo diverso da negozio a negozio, la prima volta l'utente dovrebbe aggiungere a mano il fatto che, per esempio, "bottiglia acqua" corrisponde al cibo "acqua naturale" (che sullo scontrino di un altro negozio potrebbe essere scritto diversamente, come "acqua in bottiglia"), ma facendo una seconda volta la spesa nello stesso negozio, l'app dovrebbe già conoscere questa corrispondenza.

*Nota2: se preferisce procedere con OCR (riconoscere testo da scontrino) è più interessante, ma è più difficile dal lato tecnico. Consideri che lo sforzo per integrare funzioni di questo tipo (integrare una libreria) non verrà valutato. Per semplificare, consideri che il codice a barre non è personalizzato per ogni supermercato, ad esempio la pasta barilla ha stesso codice in tutti i supermercati. Esistono API gratuite (tipo https://it.openfoodfacts.org/data) che la aiuterebbero in questo senso. In ogni caso, entrambe le soluzioni sono adeguate, proceda pure con quella che preferisce.

## features (todo)
1.	lista della spesa;
	*	[V]	list
	*	[V] add
	*	remove
2.	ricette: si può creare e salvare una ricetta;
3.	pianificazione dei pasti:
	*	[V]	si possono assegnare i cibi/ricette che si consumano a una giornata.
	*	(remove)
	*	Vengono mostrati calorie e macronutrienti per ogni alimento/giorno/settimana...;
4.	inventario:
	*	[V]	lista di oggetti posseduti,
	*	[V]	di cui si salva la quantità.
	*	Gli oggetti possono essere aggiunti automaticamente aggiungendo una spesa effettuata (per es. copiando lo scontrino, o scansionandolo con la telecamera e convertendolo in testo*),
	*	[V] (o rimossi)
		*	note: if new_quantity = 0
	*	[V] o manualmente, uno per uno (per es. per effettuare correzioni),
	*	[V] mentre vengono rimossi quando si aggiungono a un pasto;
5.	esportazione/importazione dati: si possono esportare dati (per es. ricette) come archivio - per esempio, come backup o per condividerli;
6.	condivisione dati: per es. si condivide il suddetto archivio con qualcuno (che lo potrà poi importare nell'app dal file);
7.	[V]	notifiche: si possono impostare notifiche per quando le scorte di un determinato oggetto scendono sotto una certa soglia (per es. se rimangono meno di 0,5kg di farina).
	*	nota: serve un primo avvio dell'app per farle partire; inoltre, serve di nuovo se si fa un arresto forzato
	*	nota: funziona anche al restart
8.	ricerca prezzo migliore per un prodotto, in base alle esperienze degli altri utenti: ogni utente, alla registrazione di un acquisto, invia a un'API esterna il prezzo pagato per il prodotto, e può visualizzare il negozio (tra quelli in un certo raggio dalla sua posizione) che offre al miglior prezzo un dato prodotto (soprattutto per quelli nella sua lista della spesa);
9.	suggerimento dei prodotti più convenienti in un negozio: l'utente, quando si trova in un negozio, può visualizzare quali prodotti, tra quelli nella lista della spesa, gli convenga comprare lì, e quali meglio se da un'altra parte.
10.	prodotti
*	[V]	list
*	[V]	search

*Nota: essendo gli scontrini scritti in modo diverso da negozio a negozio, la prima volta l'utente dovrebbe aggiungere a mano il fatto che, per esempio, "bottiglia acqua" corrisponde al cibo "acqua naturale" (che sullo scontrino di un altro negozio potrebbe essere scritto diversamente, come "acqua in bottiglia"), ma facendo una seconda volta la spesa nello stesso negozio, l'app dovrebbe già conoscere questa corrispondenza.

*Nota2: se preferisce procedere con OCR (riconoscere testo da scontrino) è più interessante, ma è più difficile dal lato tecnico. Consideri che lo sforzo per integrare funzioni di questo tipo (integrare una libreria) non verrà valutato. Per semplificare, consideri che il codice a barre non è personalizzato per ogni supermercato, ad esempio la pasta barilla ha stesso codice in tutti i supermercati. Esistono API gratuite (tipo https://it.openfoodfacts.org/data) che la aiuterebbero in questo senso. In ogni caso, entrambe le soluzioni sono adeguate, proceda pure con quella che preferisce.

## tasks

all list:
*	db: load only some

all viewModel:
*	```kotlin
	if(modelClass.isAssignableFrom(ListViewModel::class.java)) {
		//@Suppress("UNCHECKED_CAST")
		return ListViewModel(repository) as T
	}
	```
	
products:
*	order
*	more orders
*	filter
*	search (on full name from hierarchy)

all quantity (e.g. inventory, list):
*	default: grams
*	make more quantities (customizable)

make table with full names from hierarchy  

dialogs addTo:
*	error dialog if wrong parameters (e.g. missing quantity/or put default val)

notification:
*	 on click open app
	*	possibly on inventory
*	settings: hour of day

implementation :
*	fragments args (newinstance/constructor)
*	bundle keys in R.strings
*	use R strings formatters (e.g. list {} ...)
*	repo singleton
	*	```kotlin
	companion object {
		@Volatile
		private var instance: Repository? = null

		fun getInstance(context: Context): Repository {
			return instance ?: synchronized(this) {
				instance ?: Repository(context).also {
					instance = it
				}
			}
		}
	}
	```

repo:
*	queries dont use db's thread, why?

inventory:
*	error if add to meal more than you have

OCR:
*	fix
*	use heights to correlate lines of product - price
