## NX Commands

**Iniciar o workspace do NX vazio**

npx create-nx-workspace@latest bank-monorepo --preset=empty .

**Instalamos o Jnxplus**

npm install --save-dev @jnxplus/nx-maven

**Iniciamos o workspace do jnxplus**

npx nx generate @jnxplus/nx-maven:init

**Criar modulos**

npx nx generate @jnxplus/nx-maven:library account --language=java --groupId=com.github

**Criar apps**

npx nx generate @jnxplus/nx-maven:application account-service --language=java --framework=spring-boot --groupId=com.github