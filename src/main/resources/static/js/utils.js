// ============================================
// UTILITÁRIOS GLOBAIS
// ============================================

// Formatar CPF
function formatCPF(cpf) {
    const value = cpf.replace(/\D/g, '');
    if (value.length <= 11) {
        return value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
                    .replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3')
                    .replace(/(\d{3})(\d{3})/, '$1.$2');
    }
    return cpf;
}

// Formatar telefone
function formatTelefone(telefone) {
    const value = telefone.replace(/\D/g, '');
    if (value.length === 11) {
        return value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    }
    if (value.length === 10) {
        return value.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }
    return telefone;
}

// Formatar CEP
function formatCEP(cep) {
    const value = cep.replace(/\D/g, '');
    if (value.length === 8) {
        return value.replace(/(\d{5})(\d{3})/, '$1-$2');
    }
    return cep;
}

// Formatar data para exibição
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

// Formatar data para input date (YYYY-MM-DD)
function formatDateInput(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
}

// Formatar moeda
function formatMoney(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

// Formatar nota
function formatNota(nota) {
    if (nota === null || nota === undefined) return '-';
    return nota.toFixed(1);
}

// Obter classe de status
function getStatusClass(status) {
    const statusMap = {
        'ATIVO': 'status-success',
        'TRANCADO': 'status-warning',
        'FORMADO': 'status-info',
        'DESLIGADO': 'status-danger',
        'CURSANDO': 'status-info',
        'APROVADO': 'status-success',
        'REPROVADO': 'status-danger',
        'TRANCADA': 'status-warning'
    };
    return statusMap[status] || 'status-secondary';
}

// Atualizar para usar limite 60
function getNotaClass(nota, limite = 60) {
    if (nota >= limite) return 'status-success';
    if (nota >= limite - 20) return 'status-warning';
    return 'status-danger';
}

// Calcular média
function calcularMedia(valores) {
    if (!valores || valores.length === 0) return 0;
    const soma = valores.reduce((acc, val) => acc + val, 0);
    return soma / valores.length;
}

// Exibir alerta
function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
        <span>${message}</span>
    `;
    
    const container = document.querySelector('.content-container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);
        setTimeout(() => alertDiv.remove(), 5000);
    } else {
        alert(message);
    }
}

// Confirmar ação
function confirmAction(message) {
    return confirm(message);
}

// Mostrar loading
function showLoading(container) {
    const loadingDiv = document.createElement('div');
    loadingDiv.className = 'loading';
    loadingDiv.id = 'loading-overlay';
    loadingDiv.innerHTML = '<div class="loading-spinner"></div>';
    
    const target = container || document.querySelector('.content-container');
    if (target) {
        target.style.position = 'relative';
        target.appendChild(loadingDiv);
    }
    return loadingDiv;
}

// Esconder loading
function hideLoading(loadingDiv) {
    if (loadingDiv) loadingDiv.remove();
}

// Buscar endereço por CEP
async function buscarEnderecoPorCep(cepInput, enderecoMapping = {}) {
    const cep = cepInput.value.replace(/\D/g, '');
    if (cep.length !== 8) {
        showAlert('CEP inválido! Digite 8 dígitos.', 'error');
        return false;
    }
    
    try {
        const endereco = await EnderecoAPI.buscarCep(cep);
        
        if (endereco.logradouro && enderecoMapping.logradouro) {
            document.getElementById(enderecoMapping.logradouro).value = endereco.logradouro;
        }
        if (endereco.bairro && enderecoMapping.bairro) {
            document.getElementById(enderecoMapping.bairro).value = endereco.bairro;
        }
        if (endereco.cidade && enderecoMapping.cidade) {
            document.getElementById(enderecoMapping.cidade).value = endereco.cidade;
        }
        if (endereco.estado && enderecoMapping.estado) {
            document.getElementById(enderecoMapping.estado).value = endereco.estado;
        }
        if (endereco.complemento && enderecoMapping.complemento) {
            document.getElementById(enderecoMapping.complemento).value = endereco.complemento;
        }
        
        showAlert('Endereço preenchido automaticamente!', 'success');
        return true;
    } catch (error) {
        showAlert('Erro ao buscar CEP: ' + error.message, 'error');
        return false;
    }
}

// Máscaras para inputs
function applyMasks() {
    document.querySelectorAll('input[data-mask="cpf"]').forEach(input => {
        input.addEventListener('input', (e) => {
            e.target.value = formatCPF(e.target.value);
        });
    });
    
    document.querySelectorAll('input[data-mask="telefone"]').forEach(input => {
        input.addEventListener('input', (e) => {
            e.target.value = formatTelefone(e.target.value);
        });
    });
    
    document.querySelectorAll('input[data-mask="cep"]').forEach(input => {
        input.addEventListener('input', (e) => {
            e.target.value = formatCEP(e.target.value);
        });
    });
}

// Preencher select
function populateSelect(selectElement, items, valueField, textField, defaultOption = true) {
    if (!selectElement) return;
    
    if (defaultOption) {
        selectElement.innerHTML = '<option value="">Selecione...</option>';
    } else {
        selectElement.innerHTML = '';
    }
    
    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueField];
        option.textContent = item[textField];
        selectElement.appendChild(option);
    });
}

// Exportar tabela para CSV
function exportToCSV(tableId, filename = 'export.csv') {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const rows = table.querySelectorAll('tr');
    const csv = [];
    
    rows.forEach(row => {
        const cells = row.querySelectorAll('th, td');
        const rowData = Array.from(cells).map(cell => cell.textContent.trim());
        csv.push(rowData.join(','));
    });
    
    const blob = new Blob([csv.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
}