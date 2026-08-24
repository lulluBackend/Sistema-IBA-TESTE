// Notas Page Script
let alunoSelecionado = null;
let matriculaSelecionada = null;
let carregandoNotas = false;

async function initNotas() {
    // Aguardar o DOM estar completamente carregado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', async () => {
            await carregarAlunos();
            setupEventListeners();
        });
    } else {
        await carregarAlunos();
        setupEventListeners();
    }
}

function setupEventListeners() {
    const notaForm = document.getElementById('notaForm');
    if (notaForm) {
        notaForm.addEventListener('submit', salvarNota);
    }
    
    const filtroAluno = document.getElementById('filtroAluno');
    if (filtroAluno) {
        filtroAluno.addEventListener('change', carregarMatriculasDoAluno);
    }
    
    const filtroMatricula = document.getElementById('filtroMatricula');
    if (filtroMatricula) {
        filtroMatricula.addEventListener('change', carregarNotasDaMatricula);
    }
}

async function carregarAlunos() {
    const selectAluno = document.getElementById('filtroAluno');
    if (!selectAluno) {
        console.error('Elemento filtroAluno não encontrado');
        return;
    }
    
    try {
        const alunos = await AlunoAPI.getAll();
        
        selectAluno.innerHTML = '<option value="">Selecione um aluno</option>';
        alunos.forEach(aluno => {
            const option = document.createElement('option');
            option.value = aluno.id;
            option.textContent = `${aluno.nome} (${aluno.matricula || aluno.id})`;
            selectAluno.appendChild(option);
        });
        
        const matriculaId = sessionStorage.getItem('matriculaSelecionadaId');
        if (matriculaId) {
            sessionStorage.removeItem('matriculaSelecionadaId');
            setTimeout(async () => {
                await selecionarMatriculaPorId(matriculaId);
            }, 500);
        }
    } catch (error) {
        console.error('Erro ao carregar alunos:', error);
        showAlert('Erro ao carregar alunos: ' + error.message, 'error');
    }
}

async function selecionarMatriculaPorId(matriculaId) {
    try {
        const matricula = await MatriculaAPI.getById(matriculaId);
        if (matricula && matricula.alunoId) {
            const selectAluno = document.getElementById('filtroAluno');
            if (selectAluno) selectAluno.value = matricula.alunoId;
            
            await carregarMatriculasDoAluno();
            
            const selectMatricula = document.getElementById('filtroMatricula');
            if (selectMatricula) selectMatricula.value = matriculaId;
            
            setTimeout(() => {
                carregarNotasDaMatricula();
            }, 100);
        }
    } catch (error) {
        console.error('Erro ao selecionar matrícula:', error);
    }
}

async function carregarMatriculasDoAluno() {
    const selectAluno = document.getElementById('filtroAluno');
    const alunoId = selectAluno?.value;
    
    const selectMatricula = document.getElementById('filtroMatricula');
    const infoPanel = document.getElementById('alunoInfoPanel');
    const notasTable = document.getElementById('notasTable');
    
    if (!alunoId) {
        if (selectMatricula) selectMatricula.innerHTML = '<option value="">Selecione um aluno primeiro</option>';
        if (infoPanel) infoPanel.style.display = 'none';
        if (notasTable) notasTable.innerHTML = '<tr><td colspan="7" style="text-align: center">Selecione um aluno e matrícula</td></tr>';
        return;
    }
    
    try {
        const aluno = await AlunoAPI.getById(parseInt(alunoId));
        alunoSelecionado = aluno;
        
        if (infoPanel) infoPanel.style.display = 'block';
        const alunoInfo = document.getElementById('alunoInfo');
        if (alunoInfo) {
            alunoInfo.innerHTML = `
                <div><strong>Nome:</strong> ${aluno.nome}</div>
                <div><strong>Matrícula:</strong> ${aluno.matricula || aluno.id}</div>
                <div><strong>Curso Atual:</strong> ${aluno.cursoAtual?.nome || 'Não matriculado'}</div>
            `;
        }
        
        const matriculas = await MatriculaAPI.getByAluno(parseInt(alunoId));
        if (!selectMatricula) return;
        
        selectMatricula.innerHTML = '<option value="">Selecione uma matrícula</option>';
        
        matriculas.forEach(mat => {
            const option = document.createElement('option');
            option.value = mat.id;
            option.textContent = `${mat.cursoNome} - ${mat.ano}/${mat.semestre} (${mat.status})`;
            selectMatricula.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar matrículas:', error);
        showAlert('Erro ao carregar matrículas: ' + error.message, 'error');
    }
}

async function carregarNotasDaMatricula() {
    if (carregandoNotas) {
        console.log('Já carregando notas, ignorando...');
        return;
    }
    
    const selectMatricula = document.getElementById('filtroMatricula');
    const matriculaId = selectMatricula?.value;
    
    const selectMateria = document.getElementById('materiaNota');
    const notasTable = document.getElementById('notasTable');
    
    if (!matriculaId) {
        if (selectMateria) selectMateria.innerHTML = '<option value="">Selecione uma matrícula primeiro</option>';
        if (notasTable) notasTable.innerHTML = '<tr><td colspan="7" style="text-align: center">Selecione um aluno e matrícula</td></tr>';
        return;
    }
    
    carregandoNotas = true;
    
    try {
        matriculaSelecionada = parseInt(matriculaId);
        const matriculaAtual = document.getElementById('matriculaAtualId');
        if (matriculaAtual) matriculaAtual.value = matriculaId;
        
        const matricula = await MatriculaAPI.getById(matriculaId);
        const curso = await CursoAPI.getById(matricula.cursoId);
        
        if (selectMateria) {
            selectMateria.innerHTML = '<option value="">Selecione uma matéria</option>';
        }
        
        if (notasTable) {
            notasTable.innerHTML = '';
        }
        
        if (curso.materiasIds && curso.materiasIds.length > 0) {
            const materias = await MateriaAPI.getAll();
            const notas = await NotaAPI.getByMatricula(matriculaId);
            
            for (const materiaId of curso.materiasIds) {
                const materia = materias.find(m => m.id === materiaId);
                if (!materia) continue;
                
                if (selectMateria) {
                    const opcaoExistente = Array.from(selectMateria.options).some(opt => opt.value == materia.id);
                    if (!opcaoExistente) {
                        const option = document.createElement('option');
                        option.value = materia.id;
                        option.textContent = `${materia.nome}`;
                        selectMateria.appendChild(option);
                    }
                }
                
                const notasMateria = notas.filter(n => n.materiaId === materiaId);
                const nota1 = notasMateria.find(n => n.ciclo === 1);
                const nota2 = notasMateria.find(n => n.ciclo === 2);
                const nota3 = notasMateria.find(n => n.ciclo === 3);
                
                const valores = [nota1?.valor, nota2?.valor, nota3?.valor].filter(v => v !== undefined && v !== null);
                const media = valores.length > 0 ? valores.reduce((a, b) => a + b, 0) / valores.length : 0;
                const status = media >= 60 ? 'APROVADO' : media >= 40 ? 'RECUPERAÇÃO' : 'REPROVADO';
                const statusClass = media >= 60 ? 'status-success' : media >= 40 ? 'status-warning' : 'status-danger';
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${materia.nome}</strong></td>
                    <td><code>#${materia.id}</code></td>
                    <td style="text-align: center; font-size: 1.1rem;">
                        ${nota1 ? `<span class="status-badge ${nota1.valor >= 60 ? 'status-success' : nota1.valor >= 40 ? 'status-warning' : 'status-danger'}" style="cursor: pointer; padding: 6px 12px;" onclick="window.editarNota(${nota1.id}, 1, ${nota1.valor})">${nota1.valor.toFixed(1)}</span>` : 
                                  `<button class="btn btn-sm btn-outline" onclick="window.lancarNota(${materia.id}, 1)">📝 Lançar</button>`}
                    </td>
                    <td style="text-align: center; font-size: 1.1rem;">
                        ${nota2 ? `<span class="status-badge ${nota2.valor >= 60 ? 'status-success' : nota2.valor >= 40 ? 'status-warning' : 'status-danger'}" style="cursor: pointer; padding: 6px 12px;" onclick="window.editarNota(${nota2.id}, 2, ${nota2.valor})">${nota2.valor.toFixed(1)}</span>` : 
                                  `<button class="btn btn-sm btn-outline" onclick="window.lancarNota(${materia.id}, 2)">📝 Lançar</button>`}
                    </td>
                    <td style="text-align: center; font-size: 1.1rem;">
                        ${nota3 ? `<span class="status-badge ${nota3.valor >= 60 ? 'status-success' : nota3.valor >= 40 ? 'status-warning' : 'status-danger'}" style="cursor: pointer; padding: 6px 12px;" onclick="window.editarNota(${nota3.id}, 3, ${nota3.valor})">${nota3.valor.toFixed(1)}</span>` : 
                                  `<button class="btn btn-sm btn-outline" onclick="window.lancarNota(${materia.id}, 3)">📝 Lançar</button>`}
                    </td>
                    <td style="text-align: center; font-weight: bold; font-size: 1.1rem;">
                        <span class="status-badge ${statusClass}">${media.toFixed(1)}</span>
                    </td>
                    <td style="text-align: center;">
                        <span class="status-badge ${statusClass}">${status}</span>
                    </td>
                `;
                if (notasTable) notasTable.appendChild(tr);
            }
        } else {
            if (notasTable) {
                notasTable.innerHTML = '<tr><td colspan="7" style="text-align: center">Nenhuma matéria vinculada a este curso</td></tr>';
            }
        }
        
    } catch (error) {
        console.error('Erro detalhado:', error);
        showAlert('Erro ao carregar dados: ' + error.message, 'error');
    } finally {
        carregandoNotas = false;
    }
}

function lancarNota(materiaId, ciclo) {
    const selectMateria = document.getElementById('materiaNota');
    const cicloSelect = document.getElementById('ciclo');
    const valorInput = document.getElementById('valorNota');
    
    if (selectMateria) selectMateria.value = materiaId;
    if (cicloSelect) cicloSelect.value = ciclo;
    if (valorInput) {
        valorInput.value = '';
        valorInput.focus();
    }
    
    showAlert(`📝 Lançando nota para o ${ciclo}º ciclo. Digite a nota (0-100) e clique em Salvar.`, 'info');
    
    const form = document.getElementById('notaForm');
    if (form) form.scrollIntoView({ behavior: 'smooth' });
}

function editarNota(id, ciclo, valor) {
    const cicloSelect = document.getElementById('ciclo');
    const valorInput = document.getElementById('valorNota');
    
    if (cicloSelect) cicloSelect.value = ciclo;
    if (valorInput) {
        valorInput.value = valor;
        valorInput.focus();
    }
    
    showAlert(`✏️ Editando nota do ${ciclo}º ciclo. Valor atual: ${valor.toFixed(1)}`, 'info');
    
    const form = document.getElementById('notaForm');
    if (form) form.scrollIntoView({ behavior: 'smooth' });
}

async function salvarNota(event) {
    event.preventDefault();
    
    if (!matriculaSelecionada) {
        showAlert('❌ Selecione um aluno e matrícula primeiro!', 'error');
        return;
    }
    
    const materiaId = document.getElementById('materiaNota')?.value;
    const ciclo = parseInt(document.getElementById('ciclo')?.value);
    let valor = parseFloat(document.getElementById('valorNota')?.value);
    
    if (isNaN(valor)) {
        const valorComVirgula = document.getElementById('valorNota')?.value;
        if (valorComVirgula && valorComVirgula.includes(',')) {
            valor = parseFloat(valorComVirgula.replace(',', '.'));
        }
    }
    
    if (!materiaId) {
        showAlert('❌ Selecione uma matéria!', 'error');
        return;
    }
    
    if (isNaN(valor) || valor < 0 || valor > 100) {
        showAlert('❌ Nota inválida! Digite um valor entre 0 e 100.', 'error');
        return;
    }
    
    valor = Math.round(valor * 10) / 10;
    
    try {
        const materia = await MateriaAPI.getById(parseInt(materiaId));
        const materiaNome = materia ? materia.nome : 'Matéria';
        
        await NotaAPI.save({
            matriculaId: matriculaSelecionada,
            materiaId: parseInt(materiaId),
            ciclo: ciclo,
            valor: valor
        });
        
        showAlert(`✅ Nota ${valor.toFixed(1)} lançada com sucesso!\n📚 ${materiaNome} - ${ciclo}º ciclo`, 'success');
        
        const valorInput = document.getElementById('valorNota');
        if (valorInput) valorInput.value = '';
        
        await carregarNotasDaMatricula();
        
    } catch (error) {
        showAlert('❌ Erro ao salvar nota: ' + error.message, 'error');
    }
}

// Registrar funções no window para acesso global
window.carregarMatriculasDoAluno = carregarMatriculasDoAluno;
window.carregarNotasDaMatricula = carregarNotasDaMatricula;
window.lancarNota = lancarNota;
window.editarNota = editarNota;
window.salvarNota = salvarNota;

// Inicializar
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNotas);
} else {
    initNotas();
}