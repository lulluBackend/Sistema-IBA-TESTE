// Matriculas Page Script
let matriculasListData = [];
let carregandoMatriculas = false;

async function carregarAlunosSelect() {
    try {
        const alunos = await AlunoAPI.getAll();
        const select = document.getElementById('alunoId');
        if (!select) return;
        
        select.innerHTML = '<option value="">Selecione um aluno</option>';
        alunos.forEach(aluno => {
            const option = document.createElement('option');
            option.value = aluno.id;
            option.textContent = `${aluno.nome} (${aluno.matricula || aluno.id})`;
            select.appendChild(option);
        });
    } catch (error) {
        showAlert('Erro ao carregar alunos: ' + error.message, 'error');
    }
}

async function carregarCursosSelect() {
    try {
        const cursos = await CursoAPI.getAll();
        const select = document.getElementById('cursoId');
        if (!select) return;
        
        select.innerHTML = '<option value="">Selecione um curso</option>';
        cursos.forEach(curso => {
            const option = document.createElement('option');
            option.value = curso.id;
            option.textContent = curso.nome;
            select.appendChild(option);
        });
    } catch (error) {
        showAlert('Erro ao carregar cursos: ' + error.message, 'error');
    }
}

async function carregarMatriculas() {
    if (carregandoMatriculas) {
        console.log('Já carregando matrículas, ignorando...');
        return;
    }
    
    carregandoMatriculas = true;
    
    try {
        const matriculas = await MatriculaAPI.getAll();
        matriculasListData = matriculas;
        renderizarMatriculas(matriculas);
    } catch (error) {
        showAlert('Erro ao carregar matrículas: ' + error.message, 'error');
    } finally {
        carregandoMatriculas = false;
    }
}

function renderizarMatriculas(matriculas) {
    const tbody = document.getElementById('matriculasList');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (matriculas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center">Nenhuma matrícula encontrada</td></tr>';
        return;
    }
    
    matriculas.forEach(mat => {
        const media = mat.mediaFinal || 0;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${mat.alunoNome}</strong><br><small>${mat.alunoMatricula}</small></td>
            <td>${mat.cursoNome}</td>
            <td>${mat.ano} - ${mat.semestre}º Semestre</td>
            <td>${formatDate(mat.dataMatricula)}</td>
            <td><span class="status-badge ${getStatusClass(mat.status)}">${mat.status}</span></td>
            <td><span class="status-badge ${getNotaClass(media, 60)}">${media.toFixed(1)}</span></td>
            <td class="actions">
                <button class="btn btn-sm btn-outline" onclick="verMatricula(${mat.id})" title="Detalhes">
                    <i class="fas fa-eye"></i>
                </button>
                ${mat.status === 'CURSANDO' ? 
                    `<button class="btn btn-sm btn-danger" onclick="cancelarMatricula(${mat.id}, '${mat.alunoNome}')" title="Cancelar">
                        <i class="fas fa-times-circle"></i>
                    </button>` : ''}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function buscarMatriculas() {
    const termo = document.getElementById('searchMatricula')?.value.toLowerCase();
    if (!termo) {
        renderizarMatriculas(matriculasListData);
        return;
    }
    
    const filtradas = matriculasListData.filter(m => 
        m.alunoNome.toLowerCase().includes(termo) || 
        m.cursoNome.toLowerCase().includes(termo)
    );
    renderizarMatriculas(filtradas);
}

async function salvarMatricula(event) {
    event.preventDefault();
    
    const alunoId = document.getElementById('alunoId')?.value;
    const cursoId = document.getElementById('cursoId')?.value;
    const ano = parseInt(document.getElementById('ano')?.value);
    const semestre = parseInt(document.getElementById('semestre')?.value);
    
    if (!alunoId || !cursoId) {
        showAlert('Selecione aluno e curso!', 'error');
        return;
    }
    
    try {
        await MatriculaAPI.create({ 
            alunoId: parseInt(alunoId), 
            cursoId: parseInt(cursoId), 
            ano, 
            semestre 
        });
        showAlert('Matrícula realizada com sucesso!', 'success');
        limparFormMatricula();
        carregarMatriculas();
    } catch (error) {
        showAlert('Erro ao realizar matrícula: ' + error.message, 'error');
    }
}

async function verMatricula(id) {
    try {
        const matricula = await MatriculaAPI.getById(id);
        const modalBody = document.getElementById('matriculaDetalhes');
        if (!modalBody) return;
        
        // Buscar notas da matrícula
        const notas = await NotaAPI.getByMatricula(id);
        
        // Agrupar notas por matéria para evitar duplicação
        const notasPorMateria = new Map();
        notas.forEach(nota => {
            if (!notasPorMateria.has(nota.materiaId)) {
                notasPorMateria.set(nota.materiaId, {
                    materiaNome: nota.materiaNome,
                    materiaId: nota.materiaId,
                    ciclo1: null,
                    ciclo2: null,
                    ciclo3: null
                });
            }
            const materiaNotas = notasPorMateria.get(nota.materiaId);
            if (nota.ciclo === 1) materiaNotas.ciclo1 = nota;
            if (nota.ciclo === 2) materiaNotas.ciclo2 = nota;
            if (nota.ciclo === 3) materiaNotas.ciclo3 = nota;
        });
        
        const notasHtml = Array.from(notasPorMateria.values()).map(materia => {
            const nota1 = materia.ciclo1?.valor;
            const nota2 = materia.ciclo2?.valor;
            const nota3 = materia.ciclo3?.valor;
            const valores = [nota1, nota2, nota3].filter(v => v !== undefined && v !== null);
            const media = valores.length > 0 ? valores.reduce((a, b) => a + b, 0) / valores.length : 0;
            const statusClass = media >= 60 ? 'status-success' : media >= 40 ? 'status-warning' : 'status-danger';
            
            return `
                <tr>
                    <td>${materia.materiaNome}</td>
                    <td>#${materia.materiaId}</td>
                    <td style="text-align: center">${nota1 !== undefined ? nota1.toFixed(1) : '-'}</td>
                    <td style="text-align: center">${nota2 !== undefined ? nota2.toFixed(1) : '-'}</td>
                    <td style="text-align: center">${nota3 !== undefined ? nota3.toFixed(1) : '-'}</td>
                    <td style="text-align: center"><span class="status-badge ${statusClass}">${media.toFixed(1)}</span></td>
                    <td style="text-align: center"><span class="status-badge ${statusClass}">${media >= 60 ? 'APROVADO' : media >= 40 ? 'RECUPERAÇÃO' : 'REPROVADO'}</span></td>
                </tr>
            `;
        }).join('');
        
        modalBody.innerHTML = `
            <div class="form-grid">
                <div><strong>Aluno:</strong><br>${matricula.alunoNome}</div>
                <div><strong>Matrícula:</strong><br>${matricula.alunoMatricula}</div>
                <div><strong>Curso:</strong><br>${matricula.cursoNome}</div>
                <div><strong>Período:</strong><br>${matricula.ano} - ${matricula.semestre}º Semestre</div>
                <div><strong>Data Matrícula:</strong><br>${formatDate(matricula.dataMatricula)}</div>
                <div><strong>Status:</strong><br><span class="status-badge ${getStatusClass(matricula.status)}">${matricula.status}</span></div>
                <div><strong>Média Final:</strong><br><span class="status-badge ${getNotaClass(matricula.mediaFinal, 60)}">${(matricula.mediaFinal || 0).toFixed(1)}</span></div>
            </div>
            <hr style="margin: 15px 0">
            <h4>Notas por Matéria</h4>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Matéria</th>
                            <th>ID</th>
                            <th style="text-align: center">1º Ciclo</th>
                            <th style="text-align: center">2º Ciclo</th>
                            <th style="text-align: center">3º Ciclo</th>
                            <th style="text-align: center">Média</th>
                            <th style="text-align: center">Status</th>
                        </tr>
                    </thead>
                    <tbody>${notasHtml || '<tr><td colspan="7" style="text-align: center">Nenhuma nota lançada</td></tr>'}</tbody>
                </table>
            </div>
        `;
        
        document.getElementById('matriculaModal').classList.add('active');
    } catch (error) {
        console.error('Erro ao carregar detalhes:', error);
        showAlert('Erro ao carregar detalhes: ' + error.message, 'error');
    }
}

async function cancelarMatricula(id, nomeAluno) {
    if (confirm(`⚠️ Cancelar matrícula do aluno "${nomeAluno}"?\n\nEsta ação não poderá ser desfeita.`)) {
        try {
            await MatriculaAPI.delete(id);
            showAlert('Matrícula cancelada com sucesso!', 'success');
            carregarMatriculas();
        } catch (error) {
            showAlert('Erro ao cancelar: ' + error.message, 'error');
        }
    }
}

function limparFormMatricula() {
    const alunoSelect = document.getElementById('alunoId');
    const cursoSelect = document.getElementById('cursoId');
    const anoInput = document.getElementById('ano');
    const semestreSelect = document.getElementById('semestre');
    
    if (alunoSelect) alunoSelect.selectedIndex = 0;
    if (cursoSelect) cursoSelect.selectedIndex = 0;
    if (anoInput) anoInput.value = '2024';
    if (semestreSelect) semestreSelect.value = '1';
}

function fecharModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}

// Função auxiliar para classe da nota com limite 60
function getNotaClass(nota, limite = 60) {
    if (nota >= limite) return 'status-success';
    if (nota >= limite - 20) return 'status-warning';
    return 'status-danger';
}

// Registrar funções no window para acesso global
window.verMatricula = verMatricula;
window.cancelarMatricula = cancelarMatricula;
window.fecharModal = fecharModal;

// Event Listeners
const matriculaForm = document.getElementById('matriculaForm');
if (matriculaForm) {
    matriculaForm.addEventListener('submit', salvarMatricula);
}

// Inicializar
if (document.getElementById('alunoId')) carregarAlunosSelect();
if (document.getElementById('cursoId')) carregarCursosSelect();
if (document.getElementById('matriculasList')) carregarMatriculas();