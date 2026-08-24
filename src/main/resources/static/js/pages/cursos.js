// Cursos Page Script

async function carregarMateriasSelect() {
    try {
        const materias = await MateriaAPI.getAll();
        const select = document.getElementById('materiasSelect');
        select.innerHTML = '';
        materias.forEach(materia => {
            const option = document.createElement('option');
            option.value = materia.id;
            option.textContent = `${materia.nome} (#${materia.id})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar matérias:', error);
    }
}

async function carregarCursos() {
    try {
        const cursos = await CursoAPI.getAll();
        const tbody = document.getElementById('cursosList');
        tbody.innerHTML = '';
        
        if (cursos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center">Nenhum curso cadastrado</td></tr>';
            return;
        }
        
        for (const curso of cursos) {
            const materiasHtml = (curso.materiasNomes || []).map(m => 
                `<span class="status-badge status-secondary">${m}</span>`
            ).join(' ') || 'Nenhuma';
            
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${curso.id}</td>
                <td><strong>${curso.nome}</strong></td>
                <td style="max-width: 400px;">${materiasHtml}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-outline" onclick="editarCurso(${curso.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="removerCurso(${curso.id}, '${curso.nome}')">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        }
    } catch (error) {
        showAlert('Erro ao carregar cursos: ' + error.message, 'error');
    }
}

async function salvarCurso(event) {
    event.preventDefault();
    
    const id = document.getElementById('editCursoId').value;
    const nome = document.getElementById('nome').value;
    const materiasSelect = document.getElementById('materiasSelect');
    const materiasIds = Array.from(materiasSelect.selectedOptions).map(opt => parseInt(opt.value));
    
    if (!nome) {
        showAlert('Digite o nome do curso!', 'error');
        return;
    }
    
    const cursoData = { nome, materiasIds };
    
    try {
        if (id) {
            await CursoAPI.update(id, cursoData);
            showAlert('Curso atualizado com sucesso!');
        } else {
            await CursoAPI.create(cursoData);
            showAlert('Curso cadastrado com sucesso!');
        }
        limparFormCurso();
        carregarCursos();
    } catch (error) {
        showAlert('Erro ao salvar: ' + error.message, 'error');
    }
}

async function editarCurso(id) {
    try {
        const curso = await CursoAPI.getById(id);
        document.getElementById('editCursoId').value = curso.id;
        document.getElementById('nome').value = curso.nome;
        
        // Selecionar matérias do curso
        const materiasSelect = document.getElementById('materiasSelect');
        const materias = await MateriaAPI.getAll();
        const materiasDoCurso = await CursoAPI.getById(id);
        
        Array.from(materiasSelect.options).forEach(option => {
            if (materiasDoCurso.materiasIds?.includes(parseInt(option.value))) {
                option.selected = true;
            }
        });
        
        document.getElementById('cursoForm').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        showAlert('Erro ao carregar curso: ' + error.message, 'error');
    }
}

async function removerCurso(id, nome) {
    if (confirm(`⚠️ Remover o curso "${nome}"?\n\nSó será permitido se não houver matrículas ativas.`)) {
        try {
            await CursoAPI.delete(id);
            showAlert('Curso removido com sucesso!');
            carregarCursos();
        } catch (error) {
            showAlert('Erro ao remover: ' + error.message, 'error');
        }
    }
}

function limparFormCurso() {
    document.getElementById('editCursoId').value = '';
    document.getElementById('nome').value = '';
    const materiasSelect = document.getElementById('materiasSelect');
    if (materiasSelect) {
        Array.from(materiasSelect.options).forEach(opt => opt.selected = false);
    }
}

// Event Listeners
document.getElementById('cursoForm').addEventListener('submit', salvarCurso);

// Inicializar
carregarMateriasSelect();
carregarCursos();