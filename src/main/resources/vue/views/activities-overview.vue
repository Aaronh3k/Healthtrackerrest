<template id="activities-overview">
  <div>
    <div>
      <ul class="activities-overview-list">
        <li v-for="activity in activities">
          <a :href="`/activities/${activity.id}`">{{activity.description}} ({{activity.started}})</a>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
Vue.component("activities-overview", {
  template: "#activities-overview",
  data: () => ({
    activities: [],
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/ui/activities")
          .then(res => this.activities = res.data)
          .catch(() => alert("Error while fetching activities"));
    }
  }
});
</script>