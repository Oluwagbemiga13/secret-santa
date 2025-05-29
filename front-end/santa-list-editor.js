class SantaListEditor extends SantaListManager {
    constructor() {
        super();
        this.personCount = 0;
        this._initializeEditorEventListeners();
        this.loadExistingList();
    }

    _initializeEditorEventListeners() {
        const addButton = document.getElementById('add-person-button');
        if (addButton) {
            addButton.addEventListener('click', () => this.addPerson());
        }

        const saveButton = document.getElementById('save-list-button');
        if (saveButton) {
            saveButton.addEventListener('click', () => this.saveList());
        }

        const cardsContainer = document.querySelector('.cards-container');
        if (cardsContainer) {
            cardsContainer.addEventListener('click', (e) => {
                if (e.target.classList.contains('remove-person')) {
                    this.removePerson(e.target.dataset.index);
                }
            });
        }
    }

    // ...rest of your existing editor methods...
}